package com.kelsos.mbrc.di.providers;

import com.google.inject.Provider;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class BusProvider implements Provider<Bus> {
  @Override public Bus get() {
    return new Bus(ThreadEnforcer.MAIN);
  }
}
