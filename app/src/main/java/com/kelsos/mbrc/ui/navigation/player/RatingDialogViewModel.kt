package com.kelsos.mbrc.ui.navigation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.state.models.TrackRating
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RatingDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  appState: AppState
) : ViewModel() {
  val trackRating: Flow<TrackRating> = appState.playingTrackRating

  fun changeRating(rating: Float) {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction.create(Protocol.NowPlayingRating, rating))
    }
  }

  fun loadRating() {
    viewModelScope.launch {
      userActionUseCase.perform(UserAction.create(Protocol.NowPlayingRating))
    }
  }
}
