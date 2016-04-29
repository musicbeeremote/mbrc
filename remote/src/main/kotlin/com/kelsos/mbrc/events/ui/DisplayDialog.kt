package com.kelsos.mbrc.events.ui

import android.support.annotation.IntDef

class DisplayDialog(@DialogType val dialogType: Long) {

  @IntDef(NONE, SETUP, UPGRADE, INSTALL)
  @Retention(AnnotationRetention.SOURCE)
  annotation class DialogType

  companion object {
    const val NONE = 0L
    const val SETUP = 1L
    const val UPGRADE = 2L
    const val INSTALL = 3L
  }
}
