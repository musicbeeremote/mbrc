package com.kelsos.mbrc.utilities

import com.google.inject.Singleton
import timber.log.Timber

@Singleton
class ErrorHandler {
  fun handleThrowable(throwable: Throwable) {
    Timber.e(throwable, "Something wrong")
  }
}
