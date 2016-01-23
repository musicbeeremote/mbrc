package com.kelsos.mbrc.utilities;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBusImpl implements RxBus {

  private final Subject<Object, Object> mBusSubject = new SerializedSubject<>(PublishSubject.create());

  @Override public <T> Subscription register(final Class<T> eventClass, Action1<T> onNext) {
    //noinspection unchecked
    return mBusSubject.filter(event -> event.getClass().equals(eventClass)).map(obj -> (T) obj).subscribe(onNext);
  }

  @Override public void post(Object event) {
    mBusSubject.onNext(event);
  }
}
