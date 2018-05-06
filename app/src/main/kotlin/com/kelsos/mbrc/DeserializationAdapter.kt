package com.kelsos.mbrc

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import kotlin.reflect.KClass

interface DeserializationAdapter {
  fun <T> objectify(line: String, kClass: KClass<T>): T where T : Any
  fun <T> objectify(line: String, type: JavaType): T where T : Any
  fun <T> convertValue(data: Any?, kClass: KClass<T>): T where T : Any
  fun typeFactory(): TypeFactory
}