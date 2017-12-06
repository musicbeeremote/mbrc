package com.kelsos.mbrc

import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.interfaces.SimpleLifecycle
import com.kelsos.mbrc.networking.ChangeConnectionStateEvent
import com.kelsos.mbrc.networking.MulticastConfigurationDiscovery
import com.kelsos.mbrc.networking.SocketAction.START
import com.kelsos.mbrc.networking.SocketAction.TERMINATE
import com.kelsos.mbrc.networking.SocketClient
import com.kelsos.mbrc.networking.StartLibrarySyncEvent
import com.kelsos.mbrc.networking.StartServiceDiscoveryEvent
import com.kelsos.mbrc.networking.protocol.CommandExecutor
import com.kelsos.mbrc.networking.protocol.CommandRegistration
import com.kelsos.mbrc.networking.protocol.ProtocolHandler
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import timber.log.Timber
import toothpick.Scope
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class RemoteServiceCore
@Inject constructor(
  private val commandExecutor: CommandExecutor,
  private val discovery: MulticastConfigurationDiscovery,
  private val client: SocketClient,
  private val protocolhandler: ProtocolHandler,
  private val sessionNotificationManager: SessionNotificationManager,
  private val bus: RxBus,
  private val scope: Scope
) : SimpleLifecycle {

  private lateinit var threadPoolExecutor: ExecutorService
  private var action: SyncStartAction? = null

  override fun start() {
    Timber.v("Starting remote core")
    CommandRegistration.register(commandExecutor, scope)
    threadPoolExecutor = Executors.newSingleThreadExecutor { Thread(it, "message-thread") }
    threadPoolExecutor.execute(commandExecutor)
    discovery.startDiscovery()
    client.socketManager(START)
    bus.register(this, ChangeConnectionStateEvent::class.java) {
      this.client.socketManager(it.action)
    }
    bus.register(this, StartLibrarySyncEvent::class.java) { action?.invoke() }
    bus.register(this, StartServiceDiscoveryEvent::class.java) { discovery.startDiscovery() }
  }

  override fun stop() {
    Timber.v("Stopping remote core")
    sessionNotificationManager.cancelNotification()
    CommandRegistration.unregister(commandExecutor)
    client.socketManager(TERMINATE)
    threadPoolExecutor.shutdownNow()
    bus.unregister(this)
  }

  fun setSyncStartAction(action: SyncStartAction?) {
    this.action = action
  }
}

typealias SyncStartAction = () -> Unit
