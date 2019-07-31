package com.kelsos.mbrc.common.state

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

interface State<T> {
  fun set(data: T)
  fun getValue(): T?
  fun requireValue(): T
  fun set(updateExisting: T.() -> T)
  fun observe(owner: LifecycleOwner, observer: (T) -> Unit)
  fun removeObservers(owner: LifecycleOwner)
  fun liveData(): LiveData<T>
}