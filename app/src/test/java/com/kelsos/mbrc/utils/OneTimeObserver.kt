package com.kelsos.mbrc.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListUpdateCallback

class OneTimeObserver<T>(private val handler: (T) -> Unit) : Observer<T>, LifecycleOwner {
  private val lifecycle = LifecycleRegistry(this)
  init {
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
  }

  override fun getLifecycle(): Lifecycle = lifecycle

  override fun onChanged(t: T) {
    handler(t)
    lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  }
}

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
  val observer = OneTimeObserver(handler = onChangeHandler)
  observe(observer, observer)
}

val noopListUpdateCallback = object : ListUpdateCallback {
  override fun onInserted(position: Int, count: Int) {}
  override fun onRemoved(position: Int, count: Int) {}
  override fun onMoved(fromPosition: Int, toPosition: Int) {}
  override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
