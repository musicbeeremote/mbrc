package com.kelsos.mbrc.utilities;

import rx.Subscription;
import rx.functions.Action1;

public interface RxBus {
  <T> Subscription register(Class<T> eventClass, Action1<T> onNext);

  void post(Object event);
}
