package com.kelsos.mbrc.networking.client

import com.squareup.moshi.Moshi
import javax.inject.Inject

class MessageDeserializerImpl
@Inject
constructor(
  private val moshi: Moshi
) : MessageDeserializer {
  private val adapter by lazy { moshi.adapter(SocketMessage::class.java) }

  override fun deserialize(message: String): SocketMessage {
    return checkNotNull(adapter.fromJson(message)) { "socket message should not be null" }
  }
}
