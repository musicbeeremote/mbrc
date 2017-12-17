package com.kelsos.mbrc.ui.navigation.library.genreartists

import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface GenreArtistsView : BaseView {
  fun update(data: List<ArtistEntity>)
}

interface GenreArtistsPresenter : Presenter<GenreArtistsView> {
  fun load(genre: String)
}


