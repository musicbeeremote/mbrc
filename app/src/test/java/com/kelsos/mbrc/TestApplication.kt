package com.kelsos.mbrc

import android.app.Application
import java.lang.reflect.Method
import org.robolectric.TestLifecycleApplication
import timber.log.Timber

class TestApplication : Application(), TestLifecycleApplication {

  override fun beforeTest(method: Method?) {
    Timber.plant(object : Timber.DebugTree() {
      override fun createStackElementTag(element: StackTraceElement): String {
        return with(element) {
          val thread = with(Thread.currentThread()) { name }
          val createStackElementTag = super.createStackElementTag(element)
          val className = createStackElementTag?.split("$")?.get(0)
          "($className.kt:$lineNumber)#$methodName {$thread}"
        }
      }

      override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        System.out.println("$tag: $message")
      }
    })
    setTheme(R.style.AppTheme)
  }

  override fun prepareTest(test: Any?) {
  }

  override fun afterTest(method: Method?) {
    Timber.uprootAll()
  }
}