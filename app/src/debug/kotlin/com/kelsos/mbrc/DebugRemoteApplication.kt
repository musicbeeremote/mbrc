package com.kelsos.mbrc

import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

class DebugRemoteApplication : RemoteApplication() {
  override fun installLeakCanary(): RefWatcher {
    return LeakCanary.install(this)
  }
}
