package com.kelsos.mbrc.ui.navigation.library.genreartists

import toothpick.config.Module

class GenreArtistsModule : Module() {
  init {
    bind(GenreArtistsPresenter::class.java)
        .to(GenreArtistsPresenterImpl::class.java)
        .singletonInScope()
  }
}