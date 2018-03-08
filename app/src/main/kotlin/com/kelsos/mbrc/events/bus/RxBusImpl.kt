package com.kelsos.mbrc.events.bus

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass

class RxBusImpl @Inject
constructor() : RxBus {

  init {
    Timber.v("Injecting RxBus instance %s", this)
  }

  private val serializedRelay = PublishRelay.create<Any>().toSerialized()
  private val activeSubscriptions = HashMap<Any, MutableList<Disposable>>()

  @Suppress("UNCHECKED_CAST")
  override fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit) {
    //noinspection unchecked
    val subscription = serializedRelay.filter {
      it.javaClass == eventClass
    }.map { obj -> obj as T }.subscribe(onNext)

    updateSubscriptions(receiver, subscription)
  }

  override fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean) {
    val subscription = register(eventClass, onNext, true)
    updateSubscriptions(receiver, subscription)
  }

  private fun updateSubscriptions(receiver: Any, subscription: Disposable) {
    val subscriptions: MutableList<Disposable> = activeSubscriptions[receiver] ?: LinkedList<Disposable>()
    subscriptions.add(subscription)
    activeSubscriptions.put(receiver, subscriptions)
  }

  override fun unregister(receiver: Any) {
    val subscriptions = activeSubscriptions.remove(receiver)
    if (subscriptions != null) {
      Observable.fromIterable(subscriptions).filter { !it.isDisposed }.subscribe { it.dispose() }
    }
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> register(eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean): Disposable {
    //noinspection unchecked
    val observable = serializedRelay.filter { it.javaClass == eventClass }.map { obj -> obj as T }
    val scheduler = if (main) AndroidSchedulers.mainThread() else Schedulers.trampoline()
    return observable.observeOn(scheduler).subscribe(onNext)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> observe(eventClass: KClass<T>): Observable<T> {
    return serializedRelay.filter { it.javaClass == eventClass.java }.map { obj -> obj as T }
  }

  override fun post(event: Any) {
    serializedRelay.accept(event)
  }
}