package com.kelsos.mbrc.data

import com.kelsos.mbrc.networking.client.SocketMessage

interface SerializationAdapter {
  fun stringify(message: SocketMessage): String
}
