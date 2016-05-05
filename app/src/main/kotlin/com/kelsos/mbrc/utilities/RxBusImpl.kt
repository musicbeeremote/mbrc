package com.kelsos.mbrc.utilities

import com.kelsos.mbrc.extensions.main
import rx.Observable
import rx.Subscription
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject
import java.util.*

@Suppress("UNCHECKED_CAST")
class RxBusImpl : RxBus {

  private val mBusSubject = SerializedSubject(PublishSubject.create<Any>())
  private val activeSubscriptions = HashMap<Any, MutableList<Subscription>>()

  override fun <T> register(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit) {
    val subscription = mBusSubject.filter {
      it.javaClass == eventClass
    }.map { it as T }.subscribe(onNext)

    updateSubscriptions(receiver, subscription)
  }

  private fun updateSubscriptions(item: Any, subscription: Subscription) {
    var subscriptions: MutableList<Subscription>? = activeSubscriptions[item]
    if (subscriptions == null) {
      subscriptions = LinkedList<Subscription>()
    }

    subscriptions.add(subscription)

    activeSubscriptions.put(item, subscriptions)
  }

  override fun <T> registerOnMain(receiver: Any, eventClass: Class<T>, onNext: (T) -> Unit) {
    val subscription = mBusSubject.filter { it.javaClass == eventClass }
        .map { it as T }
        .main()
        .subscribe(onNext)

    updateSubscriptions(receiver, subscription)
  }

  override fun unregister(receiver: Any) {
    val subscriptions = activeSubscriptions[receiver]
    if (subscriptions != null) {
      Observable.from(subscriptions).filter { !it.isUnsubscribed }.subscribe { it.unsubscribe() }
      activeSubscriptions.remove(subscriptions)
    }
  }

  override fun <T> register(eventClass: Class<T>, onNext: (T) -> Unit, main: Boolean): Subscription {
    //noinspection unchecked
    val observable = mBusSubject.filter { it.javaClass == eventClass }
        .map { it as T }

    return if (main) observable.main().subscribe(onNext) else observable.subscribe(onNext)
  }

  override fun post(event: Any) {
    mBusSubject.onNext(event)
  }
}
