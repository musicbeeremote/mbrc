package com.kelsos.mbrc.mvp

import rx.Subscription
import rx.subscriptions.CompositeSubscription

open class BasePresenter<T : BaseView> : Presenter<T> {
  var view: T? = null
    private set

  private val compositeSubscription = CompositeSubscription()

  internal val isAttached: Boolean
    get() = view != null

  override fun attach(view: T) {
    this.view = view
  }

  override fun detach() {
    this.view = null
    compositeSubscription.clear()
  }

  protected fun addSubcription(subscription: Subscription) {
    this.compositeSubscription.add(subscription)
  }

  fun checkIfAttached() {
    if (!isAttached) {
      throw ViewNotAttachedException()
    }
  }

  protected class ViewNotAttachedException : RuntimeException("Please call Presenter.attach(BaseView) before calling a method on the presenter")
}
