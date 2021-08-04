package com.kelsos.mbrc.features.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RatingDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  appState: AppState
) : ViewModel() {
  private val _rating: MutableStateFlow<Float> = MutableStateFlow(0f)
  val rating: Flow<Float> get() = _rating
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

  fun changeRating(rating: Float) {
    viewModelScope.launch {
      _rating.emit(rating)
      userActionUseCase.perform(UserAction.create(Protocol.NowPlayingRating, rating))
    }
  }
}
