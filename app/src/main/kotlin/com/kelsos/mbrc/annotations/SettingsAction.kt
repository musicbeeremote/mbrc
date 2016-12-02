package com.kelsos.mbrc.annotations

import android.support.annotation.IntDef

object SettingsAction {
    const val DELETE = 1
    const val EDIT = 2
    const val DEFAULT = 3
    const val NEW = 4

    @IntDef(DELETE.toLong(), EDIT.toLong(), DEFAULT.toLong(), NEW.toLong())
    @Retention(AnnotationRetention.SOURCE)
    annotation class Action
}
