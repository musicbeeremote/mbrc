package com.kelsos.mbrc.events.bus

interface RxBus {
  fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit)

  fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean)

  fun unregister(receiver: Any)

  fun <T> register(eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean)

  fun post(event: Any)
}
