package com.kelsos.mbrc.networking

import com.kelsos.mbrc.networking.SocketAction.Action

class SocketDataAvailableEvent(val data: String)

class SocketHandshakeUpdateEvent(val done: Boolean)

class SocketStatusChangedEvent(val connected: Boolean)

/**
 * Passes a [SocketMessage] to the [SocketClient] in order to send it over the TCP socket
 * to the Remote Plugin.
 */
class SendProtocolMessage(val message: SocketMessage)

/**
 * Notifies the socket client manager that the state of the socket connection must change
 * @property action The action that will be performed to the socket client connection [Action]
 */
class ChangeConnectionStateEvent(@Action val action: Int)

class StartLibrarySyncEvent

class StartServiceDiscoveryEvent
