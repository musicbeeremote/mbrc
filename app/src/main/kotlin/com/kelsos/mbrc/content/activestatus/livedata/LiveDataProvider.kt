package com.kelsos.mbrc.content.activestatus.livedata

import android.arch.lifecycle.LiveData

interface LiveDataProvider<T> {
  fun update(data: T)
  fun get(): LiveData<T>
  fun getValue(): T?
  fun requireValue(): T
  fun update(newInstance: () -> T, updateExisting: T.() -> T)
}