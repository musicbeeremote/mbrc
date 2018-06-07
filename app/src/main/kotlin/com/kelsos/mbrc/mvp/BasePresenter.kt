package com.kelsos.mbrc.mvp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import io.reactivex.disposables.CompositeDisposable

open class BasePresenter<T : BaseView> : Presenter<T>, LifecycleOwner {
  @Suppress("LeakingThis")
  private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

  override fun getLifecycle(): Lifecycle = lifecycleRegistry

  private var view: T? = null

  protected val disposables: CompositeDisposable = CompositeDisposable()

  override val isAttached: Boolean
    get() = view != null

  override fun attach(view: T) {
    this.view = view
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
  }

  override fun detach() {
    disposables.clear()
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    this.view = null
  }

  fun view(): T {
    return view ?: throw ViewNotAttachedException()
  }

  fun checkIfAttached() {
    if (!isAttached) {
      throw ViewNotAttachedException()
    }
  }

  protected class ViewNotAttachedException :
    RuntimeException("Please call Presenter.attach(BaseView) before any method on the presenter")
}