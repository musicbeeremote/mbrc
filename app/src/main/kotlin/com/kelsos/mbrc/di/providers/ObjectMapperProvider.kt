package com.kelsos.mbrc.di.providers

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.Provider

class ObjectMapperProvider : Provider<ObjectMapper> {
  override fun get(): ObjectMapper {
    return ObjectMapper()
  }
}
