package com.kelsos.mbrc.feature.playback.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.networking.protocol.actions.UserAction
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.performUserAction
import com.kelsos.mbrc.feature.settings.domain.SettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RatingDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  appState: AppStateFlow,
  settingsManager: SettingsManager
) : ViewModel() {
  private val _rating: MutableStateFlow<Float?> = MutableStateFlow(null)
  val rating: Flow<Float?> get() = _rating

  val halfStarEnabled: StateFlow<Boolean> = settingsManager.halfStarRatingFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  init {
    viewModelScope.launch {
      appState.playingTrackRating.map { it.rating }.distinctUntilChanged().collect {
        _rating.emit(it)
      }
    }
    viewModelScope.launch {
      userActionUseCase.perform(UserAction.create(Protocol.NowPlayingRating))
    }
  }

  /**
   * Changes the rating for the current track.
   * @param rating The new rating: null = clear (send empty string), 0 = bomb, 0.5-5.0 = stars
   */
  fun changeRating(rating: Float?) {
    viewModelScope.launch {
      _rating.emit(rating)
      // Send empty string for clear (null), otherwise send the numeric value
      val payload: Any = rating ?: ""
      userActionUseCase.performUserAction(Protocol.NowPlayingRating, payload)
    }
  }
}
