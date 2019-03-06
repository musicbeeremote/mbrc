package com.kelsos.mbrc.ui.dialogs

import androidx.annotation.IntDef

object OutputSelectionCodes {
  const val CONNECTION_ERROR = 1
  const val UNKNOWN_ERROR = 2

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(CONNECTION_ERROR, UNKNOWN_ERROR)
  annotation class Code
}