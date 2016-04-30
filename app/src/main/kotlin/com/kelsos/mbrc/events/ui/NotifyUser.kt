package com.kelsos.mbrc.events.ui

import com.kelsos.mbrc.extensions.empty

class NotifyUser {
  val message: String
  val resId: Int
  var isFromResource: Boolean = false
    private set

  constructor(message: String) {
    this.message = message
    this.resId = 0
    this.isFromResource = false
  }

  constructor(resId: Int) {
    this.message = String.empty
    this.resId = resId
    this.isFromResource = true
  }
}
