package com.kelsos.mbrc.di.providers

import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Provider

class ObjectMapperProvider : Provider<ObjectMapper> {
  override fun get(): ObjectMapper {
    return ObjectMapper()
  }
}
