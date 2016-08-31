package com.kelsos.mbrc.connection_manager;


import android.support.annotation.NonNull;

import toothpick.config.Module;

public class ConnectionManagerModule extends Module{
  private ConnectionManagerModule() {
    bind(ConnectionManagerPresenter.class).to(ConnectionManagerPresenterImpl.class).scope();
  }

  @NonNull public static ConnectionManagerModule create() {
    return new ConnectionManagerModule();
  }
}
