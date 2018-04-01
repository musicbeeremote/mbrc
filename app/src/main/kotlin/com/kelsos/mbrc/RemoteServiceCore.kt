package com.kelsos.mbrc

import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProvider
import com.kelsos.mbrc.interfaces.SimpleLifecycle
import com.kelsos.mbrc.networking.client.IClientConnectionManager
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCase
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import timber.log.Timber
import javax.inject.Inject

class RemoteServiceCore
@Inject
constructor(
  private val discovery: ServiceDiscoveryUseCase,
  private val clientConnectionManager: IClientConnectionManager,
  private val defaultSettingsLiveDataProvider: DefaultSettingsLiveDataProvider,
  private val sessionNotificationManager: SessionNotificationManager
) : SimpleLifecycle {

  init {
    defaultSettingsLiveDataProvider.observe(this) {
      clientConnectionManager.run {
        stop()
        start()
      }
    }
  }

  private var action: SyncStartAction? = null

  override fun start() {
    Timber.v("Starting remote core")

    discovery.discover()
    with(clientConnectionManager) {
      setOnConnectionChangeListener { }
    }
    clientConnectionManager.start()
  }

  override fun stop() {
    Timber.v("Stopping remote core")
    sessionNotificationManager.cancelNotification()
    clientConnectionManager.stop()
    defaultSettingsLiveDataProvider.removeObservers(this)
  }

  fun setSyncStartAction(action: SyncStartAction?) {
    this.action = action
  }
}

typealias SyncStartAction = () -> Unit
