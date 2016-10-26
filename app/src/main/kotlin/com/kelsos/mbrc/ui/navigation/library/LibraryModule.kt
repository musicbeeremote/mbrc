package com.kelsos.mbrc.ui.navigation.library

import toothpick.config.Module

class LibraryModule : Module() {
  init {
    bind(LibraryActivityPresenter::class.java)
        .to(LibraryActivityPresenterImpl::class.java)
        .singletonInScope()
  }
}
