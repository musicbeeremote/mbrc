package com.kelsos.mbrc.util;

import android.os.Handler;
import android.os.Looper;
import com.google.inject.Inject;
import com.squareup.otto.Bus;

public class MainThreadBusWrapper {
    private final Bus bus;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Inject public MainThreadBusWrapper(final Bus bus) {
        if (bus == null) {
            throw new IllegalArgumentException("Bus is null");
        }
        this.bus = bus;
    }

    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            bus.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override public void run() {
                    bus.post(event);
                }
            });
        }
    }

    public void register(Object object) {
        bus.register(object);
    }

    public void unregister(Object object) {
        bus.unregister(object);
    }
}

