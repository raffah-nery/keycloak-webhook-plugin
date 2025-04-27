package com.vymalo.keycloak.webhook

import org.slf4j.LoggerFactory

class AmqpWebhookFactory : AbstractWebhookEventListenerFactory(
    AmqpConnectionSingleton.INSTANCE.handler
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AmqpWebhookFactory::class.java)
    }
    
    init {
        logger.debug("Created AmqpWebhookFactory with shared connection handler")
    }
}