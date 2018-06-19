package com.kelsos.mbrc.ui.navigation.player

import com.kelsos.mbrc.content.activestatus.TrackRating
import com.kelsos.mbrc.di.bindSingletonClass
import com.kelsos.mbrc.di.module
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

val ratingDialogModule = module {
  bindSingletonClass<RatingDialogPresenter> { RatingDialogPresenterImpl::class }
}

interface RatingDialogView : BaseView {
  fun updateRating(rating: TrackRating)
}

interface RatingDialogPresenter : Presenter<RatingDialogView> {
  fun changeRating(rating: Float)
  fun loadRating()
}
