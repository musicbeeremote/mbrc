package com.kelsos.mbrc.utilities;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBusImpl implements RxBus {

  private final Subject<Object, Object> mBusSubject = new SerializedSubject<>(PublishSubject.create());

  @Override public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext, boolean main) {
    //noinspection unchecked
    Observable<T> observable = mBusSubject.filter(event -> event.getClass().equals(eventClass)).map(obj -> (T) obj);

    return main ? observable.observeOn(AndroidSchedulers.mainThread()).subscribe(onNext) : observable.subscribe(onNext);
  }

  @Override public void post(Object event) {
    mBusSubject.onNext(event);
  }
}
