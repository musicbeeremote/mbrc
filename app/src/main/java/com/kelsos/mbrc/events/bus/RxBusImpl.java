package com.kelsos.mbrc.events.bus;

import com.jakewharton.rxrelay.PublishRelay;
import com.jakewharton.rxrelay.SerializedRelay;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RxBusImpl implements RxBus {

  private SerializedRelay<Object, Object> serializedRelay = PublishRelay.create().toSerialized();
  private HashMap<Object, List<Subscription>> activeSubscriptions = new HashMap<>();

  @Override public <T> void register(Object object, final Class<T> eventClass, Action1<T> onNext) {
    //noinspection unchecked
    Subscription subscription = serializedRelay.filter(event -> event.getClass().equals(eventClass))
        .map(obj -> (T) obj)
        .subscribe(onNext);

    updateSubscriptions(object, subscription);
  }

  @Override
  public <T> void register(Object object, Class<T> eventClass, Action1<T> onNext, boolean main) {
    Subscription subscription = register(eventClass, onNext, true);
    updateSubscriptions(object, subscription);
  }

  private void updateSubscriptions(Object object, Subscription subscription) {
    List<Subscription> subscriptions = activeSubscriptions.get(object);
    if (subscriptions == null) {
      subscriptions = new LinkedList<>();
    }

    subscriptions.add(subscription);

    activeSubscriptions.put(object, subscriptions);
  }

  @Override public void unregister(Object object) {
    List<Subscription> subscriptions = activeSubscriptions.get(object);
    if (subscriptions != null) {
      Observable.from(subscriptions)
          .filter(subscription -> !subscription.isUnsubscribed())
          .subscribe(Subscription::unsubscribe);

      activeSubscriptions.remove(subscriptions);
    }
  }

  @Override public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext, boolean main) {
    //noinspection unchecked
    Observable<T> observable = serializedRelay.filter(event -> event.getClass().equals(eventClass)).map(obj -> (T) obj);

    return main ? observable.observeOn(AndroidSchedulers.mainThread()).subscribe(onNext) : observable.subscribe(onNext);
  }

  @Override public void post(Object event) {
    serializedRelay.call(event);
  }
}
