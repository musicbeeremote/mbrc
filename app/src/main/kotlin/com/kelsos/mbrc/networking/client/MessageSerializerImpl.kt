package com.kelsos.mbrc.networking.client

import com.squareup.moshi.Moshi

class MessageSerializerImpl(
  private val mapper: Moshi
) : MessageSerializer {

  private val adapter by lazy { mapper.adapter(SocketMessage::class.java) }

  override fun serialize(message: SocketMessage): String {
    return "${adapter.toJson(message)}\r\n"
  }
}
