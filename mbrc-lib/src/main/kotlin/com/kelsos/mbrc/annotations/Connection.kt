package com.kelsos.mbrc.annotations

import android.support.annotation.IntDef

object Connection {
    const val OFF = 0L
    const val ON = 1L

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(OFF, ON)
    annotation class Status
}
