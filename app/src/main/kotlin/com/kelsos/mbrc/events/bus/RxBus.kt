package com.kelsos.mbrc.events.bus

import rx.Subscription

interface RxBus {
  fun <T> register(`object`: Any, eventClass: Class<T>, onNext: (T) -> Unit)

  fun <T> register(`object`: Any, eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean)

  fun unregister(`object`: Any)

  fun <T> register(eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean): Subscription

  fun post(event: Any)
}
