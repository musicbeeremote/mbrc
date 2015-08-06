package com.kelsos.mbrc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.kelsos.mbrc.providers.ObjectMapperProvider;
import com.kelsos.mbrc.providers.RemoteApiProvider;
import com.kelsos.mbrc.providers.RestAdapterBuilderProvider;
import com.kelsos.mbrc.rest.RemoteApi;
import retrofit.RestAdapter;

@SuppressWarnings("UnusedDeclaration") public class RemoteModule extends AbstractModule {
  public void configure() {
    bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
    bind(RestAdapter.Builder.class).toProvider(RestAdapterBuilderProvider.class).asEagerSingleton();
    bind(RemoteApi.class).toProvider(RemoteApiProvider.class).asEagerSingleton();
  }
}
