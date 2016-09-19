package com.kelsos.mbrc.events.bus

import com.jakewharton.rxrelay.PublishRelay
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class RxBusImpl @Inject
constructor() : RxBus {
  init {
    Timber.v("Injecting RxBus instance %s", this)
  }

  private val serializedRelay = PublishRelay.create<Any>().toSerialized()
  private val activeSubscriptions = HashMap<Any, List<Subscription>>()

  override fun <T> register(`object`: Any, eventClass: Class<T>, onNext: Action1<T>) {
    //noinspection unchecked
    val subscription = serializedRelay.filter { event -> event.javaClass == eventClass }.map<T>({ obj -> obj }).subscribe(onNext)

    updateSubscriptions(`object`, subscription)
  }

  override fun <T> register(`object`: Any, eventClass: Class<T>, onNext: Action1<T>, main: Boolean) {
    val subscription = register(eventClass, onNext, true)
    updateSubscriptions(`object`, subscription)
  }

  private fun updateSubscriptions(`object`: Any, subscription: Subscription) {
    var subscriptions: MutableList<Subscription>? = activeSubscriptions[`object`]
    if (subscriptions == null) {
      subscriptions = LinkedList<Subscription>()
    }

    subscriptions.add(subscription)

    activeSubscriptions.put(`object`, subscriptions)
  }

  override fun unregister(`object`: Any) {
    val subscriptions = activeSubscriptions.remove(`object`)
    if (subscriptions != null) {
      Observable.from(subscriptions).filter { subscription -> !subscription.isUnsubscribed }.subscribe { it.unsubscribe() }
    }
  }

  override fun <T> register(eventClass: Class<T>, onNext: Action1<T>, main: Boolean): Subscription {
    //noinspection unchecked
    val observable = serializedRelay.filter { event -> event.javaClass == eventClass }.map<T>({ obj -> obj })

    return if (main) observable.observeOn(AndroidSchedulers.mainThread()).subscribe(onNext) else observable.subscribe(onNext)
  }

  override fun post(event: Any) {
    serializedRelay.call(event)
  }
}
