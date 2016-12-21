package com.kelsos.mbrc

import org.robolectric.TestLifecycleApplication
import java.lang.reflect.Method


class TestApplication : RemoteApplication(), TestLifecycleApplication {

  override fun initialize() {
    initializeToothpick(true)
  }

  override fun beforeTest(method: Method?) {

  }

  override fun prepareTest(test: Any?) {

  }

  override fun afterTest(method: Method?) {

  }
}
