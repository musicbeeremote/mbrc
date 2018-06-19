package com.kelsos.mbrc.ui.navigation.player

import com.kelsos.mbrc.content.activestatus.livedata.TrackRatingLiveDataProvider
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import javax.inject.Inject

class RatingDialogPresenterImpl
@Inject
constructor(
  private val userActionUseCase: UserActionUseCase,
  private val trackRatingLiveDataProvider: TrackRatingLiveDataProvider
) : RatingDialogPresenter, BasePresenter<RatingDialogView>() {

  init {
    trackRatingLiveDataProvider.observe(this) {
      view().updateRating(it)
    }
  }

  override fun changeRating(rating: Float) {
    userActionUseCase.perform(UserAction.create(Protocol.NowPlayingRating, rating))
  }

  override fun loadRating() {
    userActionUseCase.perform(UserAction.create(Protocol.NowPlayingRating))
  }
}