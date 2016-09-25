package com.kelsos.mbrc.events.ui

import android.text.TextUtils

class CoverChangedEvent(val path: String = "") {
  val available: Boolean
    get() = TextUtils.isEmpty(this.path).not()
}
