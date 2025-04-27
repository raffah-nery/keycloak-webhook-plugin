package com.vymalo.keycloak.webhook.helper

const val eventsTakenKey = "WEBHOOK_EVENTS_TAKEN"

const val httpBaseBathKey = "WEBHOOK_HTTP_BASE_PATH"
const val httpAuthUsernameKey = "WEBHOOK_HTTP_AUTH_USERNAME"
const val httpAuthPasswordKey = "WEBHOOK_HTTP_AUTH_PASSWORD"

const val amqpUsernameKey = "WEBHOOK_AMQP_USERNAME"
const val amqpPasswordKey = "WEBHOOK_AMQP_PASSWORD"
const val amqpHostKey = "WEBHOOK_AMQP_HOST"
const val amqpPortKey = "WEBHOOK_AMQP_PORT"
const val amqpVHostKey = "WEBHOOK_AMQP_VHOST"
const val amqpExchangeKey = "WEBHOOK_AMQP_EXCHANGE"
const val amqpSsl = "WEBHOOK_AMQP_SSL"

// Novas configurações para gerenciamento de conexões
const val amqpConnectionTimeoutKey = "WEBHOOK_AMQP_CONNECTION_TIMEOUT"
const val amqpHeartbeatKey = "WEBHOOK_AMQP_HEARTBEAT"
const val amqpAutomaticRecoveryKey = "WEBHOOK_AMQP_AUTOMATIC_RECOVERY"
const val amqpTopologyRecoveryEnabledKey = "WEBHOOK_AMQP_TOPOLOGY_RECOVERY_ENABLED"
const val amqpConnectionRecoveryEnabledKey = "WEBHOOK_AMQP_CONNECTION_RECOVERY_ENABLED"
const val amqpNetworkRecoveryIntervalKey = "WEBHOOK_AMQP_NETWORK_RECOVERY_INTERVAL"
const val amqpRequestedChannelMaxKey = "WEBHOOK_AMQP_REQUESTED_CHANNEL_MAX"
const val amqpRequestedFrameMaxKey = "WEBHOOK_AMQP_REQUESTED_FRAME_MAX"

private fun getConfig(key: String): String? = System.getenv(key) ?: System.getProperty(key)

fun String.cf() = getConfig(this)
fun String.bf(compare: String = "true") = this.cf() == compare
fun String.cff() = getConfig(this)!!