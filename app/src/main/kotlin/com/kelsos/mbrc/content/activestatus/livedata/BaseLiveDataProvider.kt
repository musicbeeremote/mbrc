package com.kelsos.mbrc.content.activestatus.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class BaseLiveDataProvider<T> : LiveDataProvider<T> where T : Any {

  private val liveData: MutableLiveData<T> = MutableLiveData()

  override fun requireValue(): T {
    return checkNotNull(liveData.value) { "value should not be null" }
  }

  override fun update(data: T) {
    liveData.postValue(data)
  }

  override fun get(): LiveData<T> = liveData

  override fun getValue(): T? = liveData.value

  override fun update(updateExisting: T.() -> T) {
    liveData.postValue(updateExisting(requireValue()))
  }
}