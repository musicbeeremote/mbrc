package com.kelsos.mbrc

class MockApplication : RemoteApplication() {
  override fun testMode(): Boolean = true
}
