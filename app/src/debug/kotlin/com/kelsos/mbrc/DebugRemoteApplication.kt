package com.kelsos.mbrc

import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

class DebugRemoteApplication : RemoteApplication() {
  override fun installLeakCanary(): RefWatcher {
    return LeakCanary.install(this)
  }

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
