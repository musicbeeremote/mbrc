package com.kelsos.mbrc.ui.minicontrol

import com.kelsos.mbrc.di.bindSingletonClass
import com.kelsos.mbrc.di.module

val miniControlModule = module {
  bindSingletonClass<MiniControlPresenter> { MiniControlPresenterImpl::class }
}
