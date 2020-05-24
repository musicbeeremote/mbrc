package com.kelsos.mbrc.networking.discovery

import arrow.core.Either
import arrow.core.right
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.IOException

suspend fun <T> retryIO(
  times: Int = Int.MAX_VALUE,
  initialDelay: Long = 100,
  maxDelay: Long = 1000,
  factor: Double = 2.0,
  block: suspend () -> T
): Either<Throwable, T> {
  var currentDelay = initialDelay
  repeat(times - 1) {
    try {
      val value = block()
      return value.right()
    } catch (e: IOException) {
      // you can log an error here and/or make a more finer-grained
      // analysis of the cause to see if retry is needed
      Timber.v(e)
    }
    delay(currentDelay)
    currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
  }
  return Either.catch { block() } // last attempt
}
