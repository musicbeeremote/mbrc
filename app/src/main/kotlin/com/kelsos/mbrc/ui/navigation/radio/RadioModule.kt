package com.kelsos.mbrc.ui.navigation.radio

import com.kelsos.mbrc.di.bindSingletonClass
import toothpick.config.Module

class RadioModule : Module() {
  init {
    bindSingletonClass<RadioPresenter> { RadioPresenterImpl::class }
  }
}
