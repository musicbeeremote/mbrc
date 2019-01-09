package com.kelsos.mbrc.networking

import com.kelsos.mbrc.networking.client.SocketMessage
import io.reactivex.Single

interface RequestManager {
  fun openConnection(handshake: Boolean = true): ActiveConnection
  fun request(connection: ActiveConnection, message: SocketMessage): Single<String>
}
