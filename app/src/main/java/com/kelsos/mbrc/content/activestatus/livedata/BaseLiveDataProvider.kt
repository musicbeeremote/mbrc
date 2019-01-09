package com.kelsos.mbrc.content.activestatus.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

abstract class BaseLiveDataProvider<T> : LiveDataProvider<T> where T : Any {

  private val liveData: MutableLiveData<T> = MutableLiveData()

  override fun requireValue(): T {
    return checkNotNull(liveData.value) { "value should not be null" }
  }

  override fun update(data: T) {
    liveData.postValue(data)
  }

  override fun getValue(): T? = liveData.value

  override fun update(updateExisting: T.() -> T) {
    liveData.postValue(updateExisting(requireValue()))
  }

  override fun observe(owner: LifecycleOwner, observer: (T) -> Unit) {
    return liveData.observe(owner, Observer {
      if (it != null) {
        observer(it)
      }
    })
  }

  override fun removeObservers(owner: LifecycleOwner) {
    liveData.removeObservers(owner)
  }
}