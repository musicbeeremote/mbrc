package com.kelsos.mbrc.di.providers;

import android.app.Application;
import android.content.Context;
import javax.inject.Inject;
import javax.inject.Provider;

public class ContextProvider implements Provider<Context> {
  private Application application;

  @Inject
  public ContextProvider(Application application) {
    this.application = application;
  }

  @Override
  public Context get() {
    return application.getApplicationContext();
  }
}
