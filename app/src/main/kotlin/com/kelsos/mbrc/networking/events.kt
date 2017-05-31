package com.kelsos.mbrc.networking

class SocketDataAvailableEvent(val data: String)

class SocketHandshakeUpdateEvent(val done: Boolean)

class SocketStatusChangedEvent(val connected: Boolean)

/**
 * Passes a [SocketMessage] to the [SocketClient] in order to send it over the TCP socket
 * to the Remote Plugin.
 */
class SendProtocolMessage(val message: SocketMessage)
