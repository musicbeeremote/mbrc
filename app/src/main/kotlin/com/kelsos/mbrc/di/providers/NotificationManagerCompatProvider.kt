package com.kelsos.mbrc.di.providers

import android.app.Application
import android.support.v4.app.NotificationManagerCompat
import javax.inject.Inject
import javax.inject.Provider

class NotificationManagerCompatProvider
@Inject constructor(val context: Application) : Provider<NotificationManagerCompat> {
  override fun get(): NotificationManagerCompat {
    return NotificationManagerCompat.from(context)
  }
}
