package com.kelsos.mbrc

import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import java.util.concurrent.TimeUnit


class DebugApplication : RemoteApplication() {
  override fun installLeakCanary(): RefWatcher {
    return LeakCanary.refWatcher(this)
        .watchDelay(10, TimeUnit.SECONDS)
        .buildAndInstall()
  }
}
