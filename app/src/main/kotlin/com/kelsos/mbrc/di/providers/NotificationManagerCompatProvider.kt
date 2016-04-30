package com.kelsos.mbrc.di.providers

import android.content.Context
import android.support.v4.app.NotificationManagerCompat
import com.google.inject.Inject
import com.google.inject.Provider

class NotificationManagerCompatProvider : Provider<NotificationManagerCompat> {
  @Inject private lateinit var context: Context

  override fun get(): NotificationManagerCompat {
    return NotificationManagerCompat.from(context)
  }
}
