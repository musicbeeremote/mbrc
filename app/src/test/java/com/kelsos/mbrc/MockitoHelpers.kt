package com.kelsos.mbrc

import org.mockito.Mockito

fun <T> any(): T {
  Mockito.any<T>()
  return uninitialized()
}

fun <T> any(clazz: Class<T> ): T {
  Mockito.any<T>(clazz)
  return uninitialized()
}

private fun <T> uninitialized(): T = null as T