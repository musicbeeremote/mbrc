package com.kelsos.mbrc.content.activestatus.livedata

import androidx.lifecycle.LifecycleOwner

interface LiveDataProvider<T> {
  fun update(data: T)
  fun getValue(): T?
  fun requireValue(): T
  fun update(updateExisting: T.() -> T)
  fun observe(owner: LifecycleOwner, observer: (T) -> Unit)
  fun removeObservers(owner: LifecycleOwner)
}