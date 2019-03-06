package com.kelsos.mbrc.networking.client

import com.jakewharton.rxrelay2.PublishRelay
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.disposables.Disposable
import java.util.WeakHashMap

class UiMessageQueueImpl

constructor(
  private val appRxSchedulers: AppRxSchedulers
) : UiMessageQueue {

  private val publishRelay: PublishRelay<UiMessage> = PublishRelay.create()
  private val weakHashMap: WeakHashMap<Any, Disposable> = WeakHashMap()

  override fun dispatch(code: Int, payload: Any?) {
    publishRelay.accept(UiMessage(code, payload))
  }

  override fun observe(owner: Any, observer: (UiMessage) -> Unit) {
    weakHashMap[owner] = publishRelay.subscribeOn(appRxSchedulers.disk)
      .observeOn(appRxSchedulers.main)
      .subscribe { observer(it) }
  }

  override fun stop(owner: Any) {
    weakHashMap.remove(owner)?.dispose()
  }
}

data class UiMessage(val code: Int, val payload: Any?)