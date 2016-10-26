package com.kelsos.mbrc.di.providers

import com.fasterxml.jackson.databind.ObjectMapper
import javax.inject.Inject
import javax.inject.Provider

class ObjectMapperProvider
@Inject
constructor() : Provider<ObjectMapper> {
  override fun get(): ObjectMapper {
    return ObjectMapper()
  }
}
