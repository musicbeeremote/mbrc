package com.kelsos.mbrc

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import javax.inject.Inject
import kotlin.reflect.KClass

class DeserializationAdapterImpl
@Inject constructor(private val objectMapper: ObjectMapper) : DeserializationAdapter {
  override fun <T : Any> objectify(line: String, type: JavaType): T {
    return objectMapper.readValue(line, type)
  }

  override fun typeFactory(): TypeFactory {
    return objectMapper.typeFactory
  }

  override fun <T : Any> convertValue(data: Any?, kClass: KClass<T>): T {
    return objectMapper.convertValue(data, kClass.java)
  }

  override fun <T : Any> objectify(line: String, kClass: KClass<T>): T {
    return objectMapper.readValue(line, kClass.java)
  }
}
