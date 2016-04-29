package com.kelsos.mbrc.utilities

import rx.Subscription
import rx.functions.Action1

interface RxBus {
  fun <T> register(receiver: Any, eventClass: Class<T>, onNext: Action1<T>)

  fun <T> registerOnMain(receiver: Any, eventClass: Class<T>, onNext: Action1<T>)

  fun unregister(receiver: Any)

  fun <T> register(eventClass: Class<T>, onNext: Action1<T>, main: Boolean): Subscription

  fun post(event: Any)
}
