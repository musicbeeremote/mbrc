package com.kelsos.mbrc;

import com.google.inject.AbstractModule;
import com.kelsos.mbrc.providers.*;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.RemoteClient;
import com.squareup.otto.Bus;
import org.codehaus.jackson.map.ObjectMapper;
import retrofit.RestAdapter;

public class RemoteModule extends AbstractModule {
    @Override
    public void configure() {

        bind(Bus.class)
                .toProvider(BusProvider.class)
                .asEagerSingleton();

        bind(ObjectMapper.class)
                .toProvider(ObjectMapperProvider.class)
                .asEagerSingleton();

        bind(RestAdapter.Builder.class)
                .toProvider(RestAdapterBuilderProvider.class)
                .asEagerSingleton();

        bind(RemoteApi.class)
                .toProvider(RemoteApiProvider.class)
                .asEagerSingleton();

        bind(RemoteClient.class)
                .toProvider(RemoteClientProvider.class)
                .asEagerSingleton();
    }
}
