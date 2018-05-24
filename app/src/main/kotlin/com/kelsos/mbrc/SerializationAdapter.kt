package com.kelsos.mbrc

import com.kelsos.mbrc.networking.client.SocketMessage

interface SerializationAdapter {
  fun stringify(message: SocketMessage): String
}