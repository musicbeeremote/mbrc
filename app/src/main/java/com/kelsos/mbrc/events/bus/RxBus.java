package com.kelsos.mbrc.events.bus;

import rx.Subscription;
import rx.functions.Action1;

public interface RxBus {
  <T> void register(Object object, Class<T> eventClass, Action1<T> onNext);

  <T> void register(Object object, Class<T> eventClass, Action1<T> onNext, boolean main);

  void unregister(Object object);

  <T> Subscription register(Class<T> eventClass, Action1<T> onNext, boolean main);

  void post(Object event);
}
