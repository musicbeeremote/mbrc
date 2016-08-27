package com.kelsos.mbrc;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class DebugApplication extends RemoteApplication {
  @Override
  public void onCreate() {
    super.onCreate();
    Application application = this;

    Stetho.initialize(
        Stetho.newInitializerBuilder(application)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(application))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(application))
            .build());
  }
}
