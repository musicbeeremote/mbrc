package com.kelsos.mbrc.mvp

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

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
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    this.view = null
    CompositeDisposable().clear()
  }

  @Deprecated(
    message = "use rxkotlin +=",
    replaceWith = ReplaceWith(
      "disposables += disposable",
      imports = ["io.reactivex.rxkotlin.plusAssign"]
    )
  )
  protected fun addDisposable(disposable: Disposable) {
    CompositeDisposable().add(disposable)
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