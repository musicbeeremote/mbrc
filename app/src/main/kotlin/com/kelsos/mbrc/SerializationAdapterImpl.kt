package com.kelsos.mbrc

import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Inject

class SerializationAdapterImpl
@Inject
constructor(private val objectMapper: ObjectMapper) : SerializationAdapter {
  override fun stringify(`object`: Any): String {
    return objectMapper.writeValueAsString(`object`)
  }
}
