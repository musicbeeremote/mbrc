package com.kelsos.mbrc.utilities

import rx.Subscription

interface RxBus {
  fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit)

  fun <T> registerOnMain(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit)

  fun unregister(receiver: Any)

  fun <T> register(eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean): Subscription

  fun post(event: Any)
}
