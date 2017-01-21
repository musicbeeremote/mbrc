package com.kelsos.mbrc

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.multidex.MultiDex
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnitRunner
import com.linkedin.android.testbutler.TestButler


class MockTestRunner : AndroidJUnitRunner() {
  override fun onStart() {
    TestButler.setup(InstrumentationRegistry.getTargetContext())
    super.onStart()
  }

  override fun onCreate(arguments: Bundle?) {
    MultiDex.install(targetContext)
    super.onCreate(arguments)
  }

  override fun finish(resultCode: Int, results: Bundle) {
    TestButler.teardown(InstrumentationRegistry.getTargetContext())
    super.finish(resultCode, results)
  }

  @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
  override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
    return super.newApplication(cl, MockApplication::class.java.getName(), context)
  }
}
