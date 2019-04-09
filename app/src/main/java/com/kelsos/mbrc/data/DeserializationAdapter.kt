package com.kelsos.mbrc.data

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

interface DeserializationAdapter {
  fun <T> objectify(line: String, kClass: KClass<T>): T where T : Any
  fun <T> objectify(line: String, type: ParameterizedType): T where T : Any
  fun <T> convertValue(data: Any?, kClass: KClass<T>): T where T : Any
}