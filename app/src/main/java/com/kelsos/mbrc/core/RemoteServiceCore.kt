package com.kelsos.mbrc.core

import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.DefaultSettingsLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionLiveDataProvider
import com.kelsos.mbrc.networking.client.IClientConnectionManager
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.platform.mediasession.INotificationManager
import timber.log.Timber

class RemoteServiceCore(
  private val clientConnectionManager: IClientConnectionManager,
  private val notificationManager: INotificationManager,
  private val playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  private val playerStatusLiveDataProvider: PlayerStatusLiveDataProvider,
  private val connectionStatusLiveDataProvider: ConnectionStatusLiveDataProvider,
  private val positionLiveDataProvider: TrackPositionLiveDataProvider,
  private val defaultSettingsLiveDataProvider: DefaultSettingsLiveDataProvider
) : IRemoteServiceCore, LifeCycleAwareService() {

  init {
    defaultSettingsLiveDataProvider.observe(this) {
      Timber.v("settings changed")
      clientConnectionManager.setDefaultConnectionSettings(it)
      clientConnectionManager.start()
    }

    playingTrackLiveDataProvider.observe(this) {
      notificationManager.trackChanged(it)
    }

    playerStatusLiveDataProvider.observe(this) {
      notificationManager.playerStateChanged(it.state)
      positionLiveDataProvider.setPlaying(it.isPlaying())
    }

    connectionStatusLiveDataProvider.observe(this) {
      notificationManager.connectionStateChanged(it.status == Connection.ACTIVE)

      if (it.status != Connection.OFF) {
        return@observe
      }

      playerStatusLiveDataProvider.update {
        copy(state = PlayerState.UNDEFINED)
      }
      positionLiveDataProvider.update {
        copy(current = 0)
      }
      notificationManager.cancel()
    }
  }

  private var action: SyncStartAction? = null

  override fun start() {
    super.start()
    Timber.v("Starting remote core")
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