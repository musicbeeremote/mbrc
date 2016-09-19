package com.kelsos.mbrc.events.ui

import android.support.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

class DisplayDialog(@DialogType val dialogType: Int) {

  @IntDef(NONE.toLong(), SETUP.toLong(), UPGRADE.toLong(), INSTALL.toLong())
  @Retention(RetentionPolicy.SOURCE)
  annotation class DialogType

  companion object {
    val NONE = 0
    val SETUP = 1
    val UPGRADE = 2
    val INSTALL = 3
  }
}
