package com.kelsos.mbrc.mvp

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<T : BaseView> : Presenter<T> {
  var view: T? = null
    private set

  private val compositeDisposable = CompositeDisposable()

  override val isAttached: Boolean
    get() = view != null

  override fun attach(view: T) {
    this.view = view
  }

  override fun detach() {
    this.view = null
    compositeDisposable.clear()
  }

  protected fun addDisposable(disposable: Disposable) {
    this.compositeDisposable.add(disposable)
  }

  fun view(): T {
    return view ?: throw ViewNotAttachedException()
  }

  fun checkIfAttached() {
    if (!isAttached) {
      throw ViewNotAttachedException()
    }
  }

  protected class ViewNotAttachedException : RuntimeException("Please call Presenter.attach(BaseView) before calling a method on the presenter")
}
