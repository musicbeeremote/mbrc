package com.kelsos.mbrc.platform

import android.app.Notification

interface ForegroundHooks {
  fun start(id: Int, notification: Notification)

  fun stop()
}