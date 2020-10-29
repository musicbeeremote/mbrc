package com.kelsos.mbrc.networking

import com.kelsos.mbrc.data.SocketMessage

interface RequestManager {
  fun openConnection(handshake: Boolean = true): ActiveConnection
  fun request(connection: ActiveConnection, message: SocketMessage): String
}
