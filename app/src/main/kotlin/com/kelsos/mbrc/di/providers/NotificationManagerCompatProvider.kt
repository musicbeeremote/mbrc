package com.kelsos.mbrc.di.providers

import android.app.Application
import android.support.v4.app.NotificationManagerCompat
import javax.inject.Inject
import javax.inject.Provider

class NotificationManagerCompatProvider : Provider<NotificationManagerCompat> {
  @Inject lateinit var context: Application

  override fun get(): NotificationManagerCompat {
    return NotificationManagerCompat.from(context)
  }
}
