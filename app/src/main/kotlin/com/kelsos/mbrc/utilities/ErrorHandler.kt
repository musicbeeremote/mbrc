package com.kelsos.mbrc.utilities

import javax.inject.Singleton
import timber.log.Timber
import java.net.ConnectException

@Singleton
class ErrorHandler {
  fun handleThrowable(throwable: Throwable) {
    if (throwable is ConnectException) {
      Timber.v("Failed to connect")
    } else {
      Timber.e(throwable, "Something wrong")
    }
  }
}
