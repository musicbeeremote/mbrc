package com.kelsos.mbrc.mvp

import com.kelsos.mbrc.mvp.BaseView

interface Presenter<in T : BaseView> {
  fun attach(view: T)

  fun detach()
}
