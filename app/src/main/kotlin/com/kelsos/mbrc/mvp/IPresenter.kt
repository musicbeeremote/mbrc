package com.kelsos.mbrc.mvp

interface IPresenter<T>  where T : IView {
  fun attachView(view: T)
  fun detachView()
}

