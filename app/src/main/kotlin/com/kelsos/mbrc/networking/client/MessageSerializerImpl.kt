package com.kelsos.mbrc.networking.client

import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Inject

class MessageSerializerImpl
@Inject
constructor(private val mapper: ObjectMapper) :
  MessageSerializer {
  override fun serialize(message: SocketMessage): String {
    return "${mapper.writeValueAsString(message)}\r\n"
  }
}