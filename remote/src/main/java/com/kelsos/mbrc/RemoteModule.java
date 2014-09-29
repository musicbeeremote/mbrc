package com.kelsos.mbrc;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.kelsos.mbrc.providers.RemoteApiProvider;
import com.kelsos.mbrc.providers.RestAdapterBuilderProvider;
import com.kelsos.mbrc.rest.RemoteApi;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import org.codehaus.jackson.map.ObjectMapper;
import retrofit.RestAdapter;

public class RemoteModule extends AbstractModule {
    @Override public void configure() {
        bind(Bus.class).toInstance(new Bus(ThreadEnforcer.ANY, "mbrcbus"));
        bind(ObjectMapper.class).in(Singleton.class);
        bind(RestAdapter.Builder.class)
                .toProvider(RestAdapterBuilderProvider.class)
                .asEagerSingleton();
        bind(RemoteApi.class)
                .toProvider(RemoteApiProvider.class)
                .asEagerSingleton();
    }
}
