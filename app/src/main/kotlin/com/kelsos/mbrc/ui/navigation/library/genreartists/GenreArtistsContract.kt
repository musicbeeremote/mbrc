package com.kelsos.mbrc.ui.navigation.library.genreartists

import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface GenreArtistsView : BaseView {
  fun update(data: FlowCursorList<Artist>)
}

interface GenreArtistsPresenter : Presenter<GenreArtistsView> {
  fun load(genre: String)
}


