package com.kelsos.mbrc

import com.facebook.stetho.Stetho

class DebugApplication : RemoteApplication() {

  override fun onCreate() {
    super.onCreate()
    Stetho.initialize(Stetho.newInitializerBuilder(this)
        .enableDumpapp({
          Stetho.DefaultDumperPluginsBuilder(this)
              .finish()
        })
        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
        .build());
  }

}
