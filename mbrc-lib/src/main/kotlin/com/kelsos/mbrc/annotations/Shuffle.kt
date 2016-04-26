package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef

object Shuffle {
    const val OFF = "off"
    const val AUTODJ = "autodj"
    const val ON = "on"
    const val TOGGLE = ""
    const val UNDEF = "undef"

    @StringDef(OFF, AUTODJ, ON, TOGGLE, UNDEF)

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class State
}
