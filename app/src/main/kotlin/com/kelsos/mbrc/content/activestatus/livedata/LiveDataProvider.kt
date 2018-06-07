package com.kelsos.mbrc.content.activestatus.livedata

import androidx.lifecycle.LiveData

interface LiveDataProvider<T> {
  fun update(data: T)
  fun get(): LiveData<T>
  fun getValue(): T?
  fun requireValue(): T
  fun update(updateExisting: T.() -> T)
}