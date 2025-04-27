package com.vymalo.keycloak.webhook

import com.google.gson.Gson
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.vymalo.keycloak.webhook.models.AmqpConfig
import org.keycloak.utils.MediaType
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.ReentrantLock

class AmqpWebhookHandler : WebhookHandler {
    private var channel: Channel? = null
    private var connection: Connection? = null
    private lateinit var exchange: String
    private lateinit var connectionFactory: ConnectionFactory
    private val connectionLock = ReentrantLock()
    private var initialized = false

    companion object {
        const val PROVIDER_ID = "webhook-amqp"
        
        @JvmStatic
        private val gson = Gson()

        @JvmStatic
        private val logger = LoggerFactory.getLogger(AmqpWebhookHandler::class.java)

        @JvmStatic
        private fun getMessageProps(className: String): BasicProperties {
            val headers: MutableMap<String, Any> = HashMap()
            headers["__TypeId__"] = className
            return BasicProperties.Builder()
                .appId("Keycloak/Kotlin")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .contentEncoding("UTF-8")
                .build()
        }

        @JvmStatic
        private fun genRoutingKey(request: WebhookPayload): String =
            "KC_CLIENT.${request.realmId}.${request.clientId ?: "xxx"}.${request.userId ?: "xxx"}.${request.type}"
    }

    /**
     * Ensures that the connection and channel are open.
     * If either is closed, it will try to reinitialize them up to 3 times.
     */
    private fun ensureConnection() {
        connectionLock.lock()
        try {
            if (connection == null || !connection!!.isOpen || channel == null || !channel!!.isOpen) {
                logger.debug("Connection or channel is not available or closed. Trying to establish a new connection.")
                
                var attempts = 0
                while (attempts < 3) {
                    attempts++
                    logger.debug("Attempting to establish connection (attempt $attempts)...")
                    try {
                        // Fecha conexões e canais existentes
                        closeChannelAndConnection()
                        
                        // Cria nova conexão e canal
                        connection = connectionFactory.newConnection()
                        channel = connection!!.createChannel()
                        
                        logger.debug("Connection attempt $attempts successful: connection.isOpen=${connection!!.isOpen}, channel.isOpen=${channel!!.isOpen}")
                        break
                    } catch (ex: Exception) {
                        logger.warn("Attempt $attempts failed to establish connection: ${ex.message}", ex)
                        if (attempts < 3) {
                            Thread.sleep(1000L) // Wait 1 second before trying again
                        }
                    }
                }
                
                if (connection == null || !connection!!.isOpen || channel == null || !channel!!.isOpen) {
                    logger.error("Unable to establish connection after $attempts attempts.")
                }
            }
        } finally {
            connectionLock.unlock()
        }
    }
    
    private fun closeChannelAndConnection() {
        runCatching {
            channel?.let {
                if (it.isOpen) {
                    it.close()
                }
            }
        }.onFailure { logger.warn("Error closing channel", it) }

        runCatching {
            connection?.let {
                if (it.isOpen) {
                    it.close()
                }
            }
        }.onFailure { logger.warn("Error closing connection", it) }
        
        channel = null
        connection = null
    }

    override fun sendWebhook(request: WebhookPayload) {
        if (!initialized) {
            logger.warn("Handler not initialized. Initializing now...")
            initHandler()
        }
        
        ensureConnection()

        if (connection == null || !connection!!.isOpen || channel == null || !channel!!.isOpen) {
            logger.warn("AMQP channel or connection is still closed. Unable to send webhook: {}", request)
            return
        }

        try {
            val requestStr = gson.toJson(request)
            channel!!.basicPublish(
                exchange,
                genRoutingKey(request),
                getMessageProps(request.javaClass.name),
                requestStr.toByteArray(StandardCharsets.UTF_8)
            )

            logger.debug("Webhook message sent: {}", request)
        } catch (ex: Exception) {
            logger.error("Failed to send webhook message", ex)
            // Try to re-establish connection for next time
            ensureConnection()
        }
    }

    override fun getId(): String = PROVIDER_ID

    override fun close() {
        connectionLock.lock()
        try {
            closeChannelAndConnection()
        } finally {
            connectionLock.unlock()
        }
    }

    @Synchronized
    override fun initHandler() {
        if (initialized) {
            logger.debug("Handler already initialized. Skipping initialization.")
            return
        }
        
        connectionLock.lock()
        try {
            val amqp = AmqpConfig.fromEnv()
            exchange = amqp.exchange

            connectionFactory = ConnectionFactory().apply {
                username = amqp.username
                password = amqp.password
                virtualHost = amqp.vHost
                host = amqp.host
                port = amqp.port.toInt()
                
                // Novas configurações
                connectionTimeout = amqp.connectionTimeout
                requestedHeartbeat = amqp.heartbeat
                isAutomaticRecoveryEnabled = amqp.automaticRecovery
                isTopologyRecoveryEnabled = amqp.topologyRecoveryEnabled
                networkRecoveryInterval = amqp.networkRecoveryInterval
                requestedChannelMax = amqp.requestedChannelMax
                requestedFrameMax = amqp.requestedFrameMax
                
                if (amqp.ssl) {
                    useSslProtocol()
                }
            }

            logger.info("AmqpWebhookHandler initialized with host={}, port={}, exchange={}", 
                amqp.host, amqp.port, amqp.exchange)
            
            // Estabelece a conexão inicial
            connection = connectionFactory.newConnection("keycloak-webhook-amqp")
            channel = connection!!.createChannel()
            
            initialized = true
            logger.info("AmqpWebhookHandler connection established successfully")
        } catch (ex: Exception) {
            logger.error("Failed to initialize AmqpWebhookHandler", ex)
        } finally {
            connectionLock.unlock()
        }
    }
}