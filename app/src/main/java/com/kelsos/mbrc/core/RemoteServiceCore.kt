package com.kelsos.mbrc.core

import com.kelsos.mbrc.content.activestatus.PlayerState
import com.kelsos.mbrc.content.activestatus.livedata.ConnectionStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayerStatusState
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
import com.kelsos.mbrc.content.activestatus.livedata.TrackPositionState
import com.kelsos.mbrc.networking.client.IClientConnectionManager
import com.kelsos.mbrc.networking.connections.Connection
import com.kelsos.mbrc.platform.mediasession.INotificationManager
import timber.log.Timber

class RemoteServiceCore(
  private val clientConnectionManager: IClientConnectionManager,
  private val notificationManager: INotificationManager,
  playingTrackLiveDataProvider: PlayingTrackState,
  playerStatusLiveDataProvider: PlayerStatusState,
  connectionStatusLiveDataProvider: ConnectionStatusState,
  private val positionLiveDataProvider: TrackPositionState
) : IRemoteServiceCore, LifeCycleAwareService() {

  init {

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

      playerStatusLiveDataProvider.set {
        copy(state = PlayerState.UNDEFINED)
      }
      positionLiveDataProvider.set {
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
  }

  override fun setSyncStartAction(action: SyncStartAction?) {
    this.action = action
  }
}

typealias SyncStartAction = () -> Unit
