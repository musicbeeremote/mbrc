package com.kelsos.mbrc.annotations

import android.support.annotation.StringDef

object LastfmState {
    const val LOVE = "Love"
    const val BAN = "Ban"
    const val NORMAL = "Normal"

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @StringDef(BAN, LOVE, NORMAL)
    annotation class State
}
