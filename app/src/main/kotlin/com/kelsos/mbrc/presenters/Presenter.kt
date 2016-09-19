package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.views.BaseView

interface Presenter<in T : BaseView> {
  fun attach(view: T)

  fun detach()
}
