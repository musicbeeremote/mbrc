package com.kelsos.mbrc.networking

interface RequestManager {
  suspend fun openConnection(handshake: Boolean = true): ActiveConnection
  suspend fun request(connection: ActiveConnection, message: SocketMessage): String
}
