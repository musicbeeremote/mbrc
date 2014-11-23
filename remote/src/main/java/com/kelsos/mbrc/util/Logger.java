package com.kelsos.mbrc.util;

import roboguice.util.Ln;

public class Logger {
    public static void ProcessThrowable(Throwable throwable){
        Ln.d(throwable);
    }
}
