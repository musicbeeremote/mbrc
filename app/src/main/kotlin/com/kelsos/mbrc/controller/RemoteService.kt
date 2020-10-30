package com.kelsos.mbrc.controller

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.kelsos.mbrc.configuration.CommandRegistration
import com.kelsos.mbrc.constants.UserInputEventType
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.messaging.NotificationService
import com.kelsos.mbrc.services.ServiceDiscovery
import com.kelsos.mbrc.utilities.RemoteBroadcastReceiver
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteService : Service(), ForegroundHooks {

  private val controllerBinder = ControllerBinder()

  @Inject
  lateinit var remoteController: RemoteController

  @Inject
  lateinit var discovery: ServiceDiscovery

  @Inject
  lateinit var receiver: RemoteBroadcastReceiver

  @Inject
  lateinit var notificationService: NotificationService

  private lateinit var threadPoolExecutor: ExecutorService
  private lateinit var scope: Scope

  override fun onBind(intent: Intent?): IBinder {
    return controllerBinder
  }

  override fun onCreate() {
    super.onCreate()
    scope = Toothpick.openScope(application)
    Toothpick.inject(this, scope)
    this.registerReceiver(receiver, receiver.filter())
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.d("Background Service::Started")
    notificationService.setForegroundHooks(this)
    CommandRegistration.register(remoteController, scope)
    threadPoolExecutor = Executors.newSingleThreadExecutor {
      Thread(it, "message-thread")
    }
    threadPoolExecutor.execute(remoteController)

    remoteController.executeCommand(MessageEvent(UserInputEventType.StartConnection))
    discovery.startDiscovery()

    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    super.onDestroy()
    this.unregisterReceiver(receiver)
    remoteController.executeCommand(MessageEvent(UserInputEventType.CancelNotification))
    remoteController.executeCommand(MessageEvent(UserInputEventType.TerminateConnection))
    CommandRegistration.unregister(remoteController)
    threadPoolExecutor.shutdownNow()
    Timber.d("Background Service::Destroyed")
    Toothpick.closeScope(this)
  }

  override fun start(id: Int, notification: Notification) {
    Timber.v("Notification is starting foreground")
    startForeground(id, notification)
  }

  override fun stop() {
    Timber.v("Notification is stopping foreground")
    stopForeground(true)
  }

  private inner class ControllerBinder : Binder() {
    val service: ControllerBinder
      @SuppressWarnings("unused")
      get() = this@ControllerBinder
  }
}
