package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef

object Queue {
    const val NEXT = "next"
    const val LAST = "last"
    const val NOW = "now"

    @StringDef(NEXT, LAST, NOW)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Action
}
