package com.kelsos.mbrc.utilities;

import android.os.Handler;
import android.os.Looper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Bus;

@Singleton
public class MainThreadBus {
  private final Bus bus;
  private final Handler handler;

  @Inject
  public MainThreadBus(final Bus bus, Handler handler) {
    this.handler = handler;
    if (bus == null) {
      throw new NullPointerException("Bus is null");
    }
    this.bus = bus;
  }

  public void post(final Object event) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      bus.post(event);
    } else {
      handler.post(() -> bus.post(event));
    }
  }

  public void register(Object object) {
    bus.register(object);
  }

  public void unregister(Object object) {
    bus.unregister(object);
  }
}

