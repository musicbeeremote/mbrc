package com.kelsos.mbrc.networking.client

import com.squareup.moshi.Moshi
import javax.inject.Inject

class MessageSerializerImpl
@Inject
constructor(
  private val mapper: Moshi
) : MessageSerializer {

  private val adapter by lazy { mapper.adapter(SocketMessage::class.java) }

  override fun serialize(message: SocketMessage): String {
    return "${adapter.toJson(message)}\r\n"
  }
}