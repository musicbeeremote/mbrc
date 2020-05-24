package com.kelsos.mbrc.networking

import com.kelsos.mbrc.networking.client.SocketMessage

interface RequestManager {
  fun openConnection(handshake: Boolean = true): ActiveConnection
  fun request(connection: ActiveConnection, message: SocketMessage): String
}