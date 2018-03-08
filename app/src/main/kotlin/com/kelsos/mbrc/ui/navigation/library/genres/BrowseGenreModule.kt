package com.kelsos.mbrc.ui.navigation.library.genres

import toothpick.config.Module

class BrowseGenreModule : Module() {
  init {
    bind(BrowseGenrePresenter::class.java)
        .to(BrowseGenrePresenterImpl::class.java)
        .singletonInScope()
  }
}