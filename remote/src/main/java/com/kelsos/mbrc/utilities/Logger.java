package com.kelsos.mbrc.utilities;

import roboguice.util.Ln;

public class Logger {
  public static void logThrowable(Throwable throwable) {
    Ln.d(throwable);
  }
}
