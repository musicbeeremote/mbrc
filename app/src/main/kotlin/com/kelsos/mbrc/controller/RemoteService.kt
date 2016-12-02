package com.kelsos.mbrc.controller

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.kelsos.mbrc.events.ChangeWebSocketStatusEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.extensions.initDBFlow
import com.kelsos.mbrc.messaging.NotificationService
import com.kelsos.mbrc.messaging.SocketMessageHandler
import com.kelsos.mbrc.net.SocketService
import com.kelsos.mbrc.receivers.PlayerActionReceiver
import com.kelsos.mbrc.receivers.StateBroadcastReceiver
import com.kelsos.mbrc.repository.ConnectionRepository
import com.kelsos.mbrc.services.ServiceDiscovery
import com.raizlabs.android.dbflow.config.FlowManager
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class RemoteService : Service(),  ForegroundHooks {

  @Inject lateinit var socket: SocketService
  @Inject lateinit var handler: SocketMessageHandler
  @Inject lateinit var receiver: StateBroadcastReceiver
  @Inject lateinit var actionReceiver: PlayerActionReceiver
  @Inject lateinit var notificationService: NotificationService
  @Inject lateinit var discovery: ServiceDiscovery
  @Inject lateinit var connectionRepository: ConnectionRepository
  @Inject lateinit var bus: RxBus
  private lateinit var scope: Scope

  init {
    Timber.d("Application Controller Initialized")
  }

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
    this.initDBFlow()
    scope = Toothpick.openScopes(application, this)
    Toothpick.inject(this, scope)

    this.registerReceiver(actionReceiver, actionReceiver.intentFilter)
    this.registerReceiver(receiver, receiver.intentFilter)
    bus.register(this,
        ChangeWebSocketStatusEvent::class.java,
        { this.onWebSocketActionRequest(it) })
    notificationService.setForegroundHooks(this)
  }


  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    Timber.v("[Service] start command received")
    discovery.startDiscovery({
      connectionRepository.default?.let {
        socket.startWebSocket()
      }
    })

    return super.onStartCommand(intent, flags, startId)
  }

  override fun onDestroy() {
    super.onDestroy()
    Timber.v("[Service] destroying service")
    bus.unregister(this)
    notificationService.cancelNotification(NotificationService.NOW_PLAYING_PLACEHOLDER)
    socket.disconnect()
    FlowManager.destroy()
    this.unregisterReceiver(receiver)
    this.unregisterReceiver(actionReceiver)
    Toothpick.closeScope(this)
  }

  private fun onWebSocketActionRequest(event: ChangeWebSocketStatusEvent) {
    when (event.action) {
      ChangeWebSocketStatusEvent.CONNECT -> {
        Timber.v("Attempting to start the websocket")
        socket.startWebSocket()
      }
      ChangeWebSocketStatusEvent.DISCONNECT -> {
        Timber.v("Attempting to stop the websocket")
        socket.disconnect()
      }
    }
  }

  override fun start(id: Int, notification: Notification) {
    Timber.v("Notification is starting foreground")
    startForeground(id, notification)
  }

  override fun stop() {
    Timber.v("Notification is stopping foreground")
    stopForeground(true)
  }

}
