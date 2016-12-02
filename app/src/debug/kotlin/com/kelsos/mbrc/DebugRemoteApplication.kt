package com.kelsos.mbrc

import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import java.util.concurrent.TimeUnit

class DebugRemoteApplication : RemoteApplication() {
  override fun installLeakCanary(): RefWatcher {
    return LeakCanary.refWatcher(this)
        .watchDelay(10, TimeUnit.SECONDS)
        .buildAndInstall()
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
