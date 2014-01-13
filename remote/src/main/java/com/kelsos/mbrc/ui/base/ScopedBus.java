package com.kelsos.mbrc.ui.base;

import com.google.inject.Inject;
import com.squareup.otto.Bus;

import java.util.HashSet;
import java.util.Set;

public class ScopedBus {
    private final Bus bus;
    private final Set<Object> objects = new HashSet<>();
    private boolean active;

    @Inject public ScopedBus(Bus bus) {
        this.bus = bus;
    }

    public void register(Object obj) {
        objects.add(obj);
        if (active) {
            bus.register(obj);
        }
    }

    public void unregister(Object obj) {
        objects.remove(obj);
        if (active) {
            bus.unregister(obj);
        }
    }

    public void post(Object event) {
        bus.post(event);
    }

    void paused() {
        active = false;
        for (Object obj : objects) {
            bus.unregister(obj);
        }
    }

    void resumed() {
        active = true;
        for (Object obj : objects) {
            bus.register(obj);
        }
    }
}
