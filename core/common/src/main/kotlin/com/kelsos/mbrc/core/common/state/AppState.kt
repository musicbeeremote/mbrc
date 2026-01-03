package com.kelsos.mbrc.core.common.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AppStateFlow {
  val playerStatus: StateFlow<PlayerStatusModel>
  val playingTrack: StateFlow<TrackInfo>
  val playingTrackRating: StateFlow<TrackRating>
  val playingPosition: StateFlow<PlayingPosition>
  val lyrics: StateFlow<List<String>>
}

interface AppStatePublisher : AppStateFlow {
  fun updatePlayerStatus(status: PlayerStatusModel)

  fun updatePlayingTrack(track: TrackInfo)

  fun updateTrackRating(rating: TrackRating)

  fun updatePlayingPosition(position: PlayingPosition)

  fun updateLyrics(lyrics: List<String>)
}

class AppState : AppStatePublisher {
  private val _playerStatus = MutableStateFlow(PlayerStatusModel())
  private val _playingTrack = MutableStateFlow<TrackInfo>(BasicTrackInfo())
  private val _playingTrackRating = MutableStateFlow(TrackRating())
  private val _playingPosition = MutableStateFlow(PlayingPosition())
  private val _lyrics = MutableStateFlow(emptyList<String>())

  override val playerStatus: StateFlow<PlayerStatusModel> = _playerStatus.asStateFlow()
  override val playingTrack: StateFlow<TrackInfo> = _playingTrack.asStateFlow()
  override val playingTrackRating: StateFlow<TrackRating> = _playingTrackRating.asStateFlow()
  override val playingPosition: StateFlow<PlayingPosition> = _playingPosition.asStateFlow()
  override val lyrics: StateFlow<List<String>> = _lyrics.asStateFlow()

  override fun updatePlayerStatus(status: PlayerStatusModel) {
    _playerStatus.value = status
  }

  override fun updatePlayingTrack(track: TrackInfo) {
    _playingTrack.value = track
  }

  override fun updateTrackRating(rating: TrackRating) {
    _playingTrackRating.value = rating
  }

  override fun updatePlayingPosition(position: PlayingPosition) {
    _playingPosition.value = position
  }

  override fun updateLyrics(lyrics: List<String>) {
    _lyrics.value = lyrics
  }
}
