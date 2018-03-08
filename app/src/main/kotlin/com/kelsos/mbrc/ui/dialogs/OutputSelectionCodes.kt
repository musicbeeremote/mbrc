package com.kelsos.mbrc.ui.dialogs

import android.support.annotation.IntDef

object OutputSelectionContract {
  const val CONNECTION_ERROR = 1
  const val UNKNOWN_ERROR = 2

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(CONNECTION_ERROR, UNKNOWN_ERROR)
  annotation class Code
}
