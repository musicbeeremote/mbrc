package com.kelsos.mbrc.networking.client

import com.fasterxml.jackson.databind.JsonNode

interface MessageDeserializer {
  fun deserialize(message: String): JsonNode
}