package com.kelsos.mbrc.utilities;

import com.google.inject.Singleton;
import timber.log.Timber;

@Singleton
public class ErrorHandler {
  public void handleThrowable(Throwable throwable) {
    Timber.e(throwable, "Something wrong");
  }
}
