package com.kelsos.mbrc

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.multidex.MultiDex
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
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

  override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
    return super.newApplication(cl, MockApplication::class.java.name, context)
  }
}