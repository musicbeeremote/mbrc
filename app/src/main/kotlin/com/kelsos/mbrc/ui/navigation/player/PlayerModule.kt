package com.kelsos.mbrc.ui.navigation.player

import com.kelsos.mbrc.di.bindSingletonClass
import com.kelsos.mbrc.di.module

val mainModule = module {
  bindSingletonClass<PlayerPresenter> { PlayerPresenterImpl::class }
}
