package com.kelsos.mbrc.feature.playback.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.state.AppStateFlow
import com.kelsos.mbrc.core.networking.protocol.actions.UserAction
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.performUserAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class RatingDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  appState: AppStateFlow
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
      userActionUseCase.performUserAction(Protocol.NowPlayingRating, rating)
    }
  }
}
