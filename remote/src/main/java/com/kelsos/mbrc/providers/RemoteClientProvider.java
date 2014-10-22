package com.kelsos.mbrc.providers;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.kelsos.mbrc.data.Model;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.RemoteClient;
import com.squareup.otto.Bus;

public class RemoteClientProvider implements Provider<RemoteClient>{
    private final Bus bus;
    private final RemoteApi api;
    private final Model model;

    @Inject
    public RemoteClientProvider(Bus bus, RemoteApi api, Model model) {
        this.bus = bus;
        this.api = api;
        this.model = model;
    }

    @Override
    public RemoteClient get() {
        return new RemoteClient(bus, api, model);
    }
}
