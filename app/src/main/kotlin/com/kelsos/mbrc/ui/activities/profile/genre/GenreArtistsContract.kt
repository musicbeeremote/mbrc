package com.kelsos.mbrc.ui.activities.profile.genre

import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.mvp.Presenter
import com.kelsos.mbrc.mvp.BaseView
import com.raizlabs.android.dbflow.list.FlowCursorList
import toothpick.config.Module

interface GenreArtistsView : BaseView {
  fun update(data: FlowCursorList<Artist>)
}

interface GenreArtistsPresenter : Presenter<GenreArtistsView> {
  fun load(genre: String)
}


class GenreArtistsModule : Module() {
  init {
    bind(GenreArtistsPresenter::class.java)
        .to(GenreArtistsPresenterImpl::class.java)
        .singletonInScope()
  }
}
