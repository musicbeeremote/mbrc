package com.kelsos.mbrc

import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.networking.client.IClientConnectionManager
import com.kelsos.mbrc.networking.discovery.ServiceDiscoveryUseCase
import com.kelsos.mbrc.platform.mediasession.INotificationManager
import timber.log.Timber
import javax.inject.Inject

class RemoteServiceCore
@Inject
constructor(
  private val discovery: ServiceDiscoveryUseCase,
  private val clientConnectionManager: IClientConnectionManager,
  private val notificationManager: INotificationManager,
  private val playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  private val defaultSettingsLiveDataProvider: DefaultSettingsLiveDataProvider
) : IRemoteServiceCore, LifeCycleAwareService() {

  init {
    defaultSettingsLiveDataProvider.observe(this) {
      Timber.v("settings changed")
      clientConnectionManager.setDefaultConnectionSettings(it)
      clientConnectionManager.start()
    }

    playingTrackLiveDataProvider.get().observe(this) {
      if (it == null) {
        return@observe
      }

      notificationManager.trackChanged(it)
    }

    playerStatusLiveDataProvider.get().observe(this) {
      if (it == null) {
        return@observe
      }

      notificationManager.playerStateChanged(it.playState)
    }
  }

  private var action: SyncStartAction? = null

  override fun start() {
    super.start()
    Timber.v("Starting remote core")
    with(clientConnectionManager) {
      setOnConnectionChangeListener {
        notificationManager.connectionStateChanged(it)
      }
    }

    discovery.discover {}
  }

  override fun stop() {
    super.stop()
    Timber.v("Stopping remote core")

    clientConnectionManager.stop()
    defaultSettingsLiveDataProvider.removeObservers(this)
  }

  override fun setSyncStartAction(action: SyncStartAction?) {
    this.action = action
  }
}

typealias SyncStartAction = () -> Unit
