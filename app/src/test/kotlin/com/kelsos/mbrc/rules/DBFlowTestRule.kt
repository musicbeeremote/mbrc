package com.kelsos.mbrc.rules

import androidx.test.core.app.ApplicationProvider
import com.kelsos.mbrc.TestApplication
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DBFlowTestRule : TestRule {
  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      @Throws(Throwable::class)
      override fun evaluate() {
        val applicationContext = ApplicationProvider.getApplicationContext<TestApplication>()
        FlowManager.init(FlowConfig.Builder(applicationContext).build())
        try {
          base.evaluate()
        } finally {
          FlowManager.reset()
          FlowManager.close()
          FlowManager.destroy()
        }
      }
    }
  }

  companion object {

    fun create(): DBFlowTestRule {
      return DBFlowTestRule()
    }
  }
}
