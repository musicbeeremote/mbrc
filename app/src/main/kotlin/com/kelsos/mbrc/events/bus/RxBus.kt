package com.kelsos.mbrc.events.bus

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlin.reflect.KClass

interface RxBus {
  fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit)

  fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean)

  fun unregister(receiver: Any)

  fun <T> register(eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean): Disposable

  fun <T : Any> observe(eventClass: KClass<T>): Observable<T>

  fun post(event: Any)
}
