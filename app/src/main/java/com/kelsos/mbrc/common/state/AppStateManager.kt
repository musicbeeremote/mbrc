package com.kelsos.mbrc.common.state

import com.kelsos.mbrc.common.state.domain.PlayerState
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.content.activestatus.PlayingTrackCache
import com.kelsos.mbrc.networking.connections.ConnectionState
import com.kelsos.mbrc.networking.connections.ConnectionStatus
import com.kelsos.mbrc.platform.mediasession.SessionNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

typealias StateHandler = (Boolean) -> Unit

class AppStateManager(
  private val appState: AppState,
  private val connectionState: ConnectionState,
  private val notifications: SessionNotificationManager,
  private val trackCache: PlayingTrackCache,
  private val dispatchers: AppCoroutineDispatchers
) {
  private var stateHandler: StateHandler? = null
  private var job = SupervisorJob()
  private var scope = CoroutineScope(job + dispatchers.io)

  init {
    scope.launch {
      val track = trackCache.restoreInfo()
      appState.playingTrack.emit(track)
    }
  }

  fun start() {
    if (job.isCancelled || job.isCompleted) {
      job = SupervisorJob()
      scope = CoroutineScope(job + dispatchers.io)
    }
    val playingPosition = appState.playingPosition
    scope.launch {
      var timer: Timer? = null
      appState.playerStatus.distinctUntilChangedBy { it.state }.map { it.state }.collect { state ->
        stateHandler?.invoke(state == PlayerState.Playing)
        val currentPosition = playingPosition.map { it.current }.distinctUntilChanged().first()
        notifications.updateState(state, currentPosition)
        timer?.cancel()
        if (state == PlayerState.Playing) {
          timer = fixedRateTimer("progress", period = UPDATE_PERIOD_MS) {
            updatePosition()
          }
        }
      }
    }

    scope.launch {
      appState.playingTrack.collect { playingTrack ->
        notifications.updatePlayingTrack(playingTrack)
        trackCache.persistInfo(playingTrack)
      }
    }

    scope.launch {
      connectionState.connection.collect { connnection ->
        notifications.connectionStateChanged(connnection == ConnectionStatus.Active)
      }
    }

    scope.launch {
      playingPosition.collect { playingPosition ->
        val playerState = appState.playerStatus.map { it.state }.first()
        notifications.updateState(playerState, playingPosition.current)
      }
    }
  }

  fun stop() {
    job.cancel()
    notifications.cancel()
  }

  private fun updatePosition() {
    scope.launch {
      val playingPosition = appState.playingPosition
      val position = playingPosition.first()
      val current = (position.current + UPDATE_PERIOD_MS).coerceAtMost(position.total)
      val newPosition = position.copy(current = current)
      playingPosition.emit(newPosition)
    }
  }

  fun setStateHandler(stateHandler: StateHandler? = null) {
    this.stateHandler = stateHandler
  }

  companion object {
    private const val UPDATE_PERIOD_MS = 1000L
  }
}
