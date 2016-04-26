package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef

object Repeat {
    const val ALL = "all"
    const val NONE = "none"
    const val ONE = "one"
    const val CHANGE = ""
    const val UNDEFINED = "undef"

    @StringDef(ALL, NONE, ONE, CHANGE, UNDEFINED)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Mode
}
