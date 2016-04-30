package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef

object Queue {
    const val NEXT = "next"
    const val LAST = "last"
    const val NOW = "now"
    const val UNDEF = "undef"

    @StringDef(NEXT, LAST, NOW, UNDEF)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Action
}
