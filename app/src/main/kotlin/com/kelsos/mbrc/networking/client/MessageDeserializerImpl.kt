package com.kelsos.mbrc.networking.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Inject

class MessageDeserializerImpl
@Inject
constructor(
  private val mapper: ObjectMapper
) : MessageDeserializer {
  override fun deserialize(message: String): JsonNode {
    return mapper.readValue(message, JsonNode::class.java)
  }
}
