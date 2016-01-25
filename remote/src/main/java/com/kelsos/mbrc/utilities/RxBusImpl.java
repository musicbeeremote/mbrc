package com.kelsos.mbrc.utilities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBusImpl implements RxBus {

  private final Subject<Object, Object> mBusSubject = new SerializedSubject<>(PublishSubject.create());
  private HashMap<Object, List<Subscription>> activeSubscriptions = new HashMap<>();

  @Override public <T> void register(Object object, final Class<T> eventClass, Action1<T> onNext) {
    Subscription subscription = mBusSubject.filter(event -> event.getClass().equals(eventClass))
        .map(obj -> (T) obj)
        .subscribe(onNext);

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

  @Override public <T> void registerOnMain(Object object, final Class<T> eventClass, Action1<T> onNext) {
    Subscription subscription = mBusSubject.filter(event -> event.getClass().equals(eventClass))
        .map(obj -> (T) obj)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(onNext);

    updateSubscriptions(object, subscription);
  }

  @Override public void unregister(Object object) {
    List<Subscription> subscriptions = activeSubscriptions.get(object);
    if (subscriptions != null) {
      Observable.from(subscriptions)
          .filter(subscription -> !subscription.isUnsubscribed())
          .subscribe(Subscription::unsubscribe);
    }
  }

  @Override public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext, boolean main) {
    //noinspection unchecked
    Observable<T> observable = mBusSubject.filter(event -> event.getClass().equals(eventClass)).map(obj -> (T) obj);

    return main ? observable.observeOn(AndroidSchedulers.mainThread()).subscribe(onNext) : observable.subscribe(onNext);
  }

  @Override public void post(Object event) {
    mBusSubject.onNext(event);
  }
}
