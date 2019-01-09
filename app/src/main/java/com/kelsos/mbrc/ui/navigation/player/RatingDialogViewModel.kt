package com.kelsos.mbrc.ui.navigation.player

import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol


class RatingDialogViewModel(
  private val userActionUseCase: UserActionUseCase,
  val trackRatingLiveDataProvider: TrackRatingLiveDataProvider
) : ViewModel() {

  fun changeRating(rating: Float) {
    userActionUseCase.perform(UserAction.create(Protocol.NowPlayingRating, rating))
  }

  fun loadRating() {
    userActionUseCase.perform(UserAction.create(Protocol.NowPlayingRating))
  }
}