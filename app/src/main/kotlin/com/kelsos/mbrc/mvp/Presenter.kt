package com.kelsos.mbrc.mvp

interface Presenter<T>  where T : BaseView {
  fun attach(view: T)
  fun detach()
}

