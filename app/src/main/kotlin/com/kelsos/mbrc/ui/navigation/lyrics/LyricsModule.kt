package com.kelsos.mbrc.ui.navigation.lyrics

import com.kelsos.mbrc.di.bindSingletonClass
import toothpick.config.Module

class LyricsModule : Module() {
  init {
    bindSingletonClass<LyricsPresenter> { LyricsPresenterImpl::class }
  }
}