package com.kelsos.mbrc.events.bus

import io.reactivex.disposables.Disposable

interface RxBus {
  fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit)

  fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean)

  fun unregister(receiver: Any)

  fun <T> register(eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean): Disposable

  fun post(event: Any)
}
