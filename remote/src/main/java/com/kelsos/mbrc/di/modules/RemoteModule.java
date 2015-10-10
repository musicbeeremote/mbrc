package com.kelsos.mbrc.di.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.kelsos.mbrc.di.providers.BusProvider;
import com.kelsos.mbrc.di.providers.ObjectMapperProvider;
import com.kelsos.mbrc.di.providers.OkHttpClientProvider;
import com.kelsos.mbrc.di.providers.RemoteApiProvider;
import com.kelsos.mbrc.presenters.MainViewPresenter;
import com.kelsos.mbrc.presenters.MiniControlPresenter;
import com.kelsos.mbrc.presenters.interfaces.IMainViewPresenter;
import com.kelsos.mbrc.presenters.interfaces.IMiniControlPresenter;
import com.kelsos.mbrc.rest.RemoteApi;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;

@SuppressWarnings("UnusedDeclaration") public class RemoteModule extends AbstractModule {
  public void configure() {
    bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
    bind(OkHttpClient.class).toProvider(OkHttpClientProvider.class).in(Singleton.class);
    bind(RemoteApi.class).toProvider(RemoteApiProvider.class).asEagerSingleton();
    bind(Bus.class).toProvider(BusProvider.class).in(Singleton.class);
    bind(IMiniControlPresenter.class).to(MiniControlPresenter.class);
    bind(IMainViewPresenter.class).to(MainViewPresenter.class);
  }
}
