package com.kelsos.mbrc.content.activestatus.livedata

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData

abstract class BaseLiveDataProvider<T> : LiveDataProvider<T> where T: Any {

  override fun requireValue(): T {
    return checkNotNull(liveData.value) { "value should not be null" }
  }

  private val liveData: MutableLiveData<T> = MutableLiveData()

  override fun update(data: T) {
    liveData.postValue(data)
  }

  override fun get(): LiveData<T> = liveData

  override fun getValue(): T? = liveData.value

  override fun update(newInstance: () -> T, updateExisting: T.() -> T) {
    val currentValue = liveData.value
    val update = if (currentValue != null) {
      updateExisting(currentValue)
    } else {
      newInstance()
    }

    liveData.postValue(update)
  }
}