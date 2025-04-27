package com.vymalo.keycloak.webhook

import org.slf4j.LoggerFactory

/**
 * Singleton que mantém uma única instância do handler de conexão AMQP.
 * Isso garante que apenas uma conexão seja criada por JVM.
 */
class AmqpConnectionSingleton private constructor() {
    val handler: AmqpWebhookHandler = AmqpWebhookHandler()
    
    companion object {
        private val logger = LoggerFactory.getLogger(AmqpConnectionSingleton::class.java)
        
        val INSTANCE: AmqpConnectionSingleton by lazy { 
            logger.info("Initializing AmqpConnectionSingleton")
            AmqpConnectionSingleton()
        }
    }
    
    init {
        logger.info("AmqpConnectionSingleton initialized")
    }
}