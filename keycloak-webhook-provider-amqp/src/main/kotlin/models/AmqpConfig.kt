package com.vymalo.keycloak.webhook.models

import com.vymalo.keycloak.webhook.helper.*

data class AmqpConfig(
    val username: String,
    val password: String,
    val host: String,
    val port: String,
    val vHost: String?,
    val ssl: Boolean,
    val exchange: String,
    val connectionTimeout: Int,
    val heartbeat: Int,
    val automaticRecovery: Boolean,
    val topologyRecoveryEnabled: Boolean,
    val connectionRecoveryEnabled: Boolean,
    val networkRecoveryInterval: Long,
    val requestedChannelMax: Int,
    val requestedFrameMax: Int
) {
    companion object {
        fun fromEnv(): AmqpConfig = AmqpConfig(
            username = amqpUsernameKey.cff(),
            password = amqpPasswordKey.cff(),
            host = amqpHostKey.cff(),
            port = amqpPortKey.cff(),
            vHost = amqpVHostKey.cf(),
            ssl = amqpSsl.bf(),
            exchange = amqpExchangeKey.cff(),
            connectionTimeout = amqpConnectionTimeoutKey.cf()?.toIntOrNull() ?: 30000,
            heartbeat = amqpHeartbeatKey.cf()?.toIntOrNull() ?: 60,
            automaticRecovery = amqpAutomaticRecoveryKey.bf(),
            topologyRecoveryEnabled = amqpTopologyRecoveryEnabledKey.bf(),
            connectionRecoveryEnabled = amqpConnectionRecoveryEnabledKey.bf(),
            networkRecoveryInterval = amqpNetworkRecoveryIntervalKey.cf()?.toLongOrNull() ?: 5000L,
            requestedChannelMax = amqpRequestedChannelMaxKey.cf()?.toIntOrNull() ?: 10,
            requestedFrameMax = amqpRequestedFrameMaxKey.cf()?.toIntOrNull() ?: 0
        )
    }
}