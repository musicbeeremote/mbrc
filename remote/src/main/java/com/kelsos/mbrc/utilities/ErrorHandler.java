package com.kelsos.mbrc.utilities;

import com.google.inject.Singleton;

import roboguice.util.Ln;

@Singleton
public class ErrorHandler {
  public void handleThrowable(Throwable throwable) {
    Ln.v(throwable);
  }
}
