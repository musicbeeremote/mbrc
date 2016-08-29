package com.kelsos.mbrc.rules;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.robolectric.RuntimeEnvironment;

public class DBFlowTestRule implements TestRule {
  @Override
  public Statement apply(Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        FlowManager.init(new FlowConfig.Builder(RuntimeEnvironment.application).build());
        try {
          base.evaluate();
        } finally {
          FlowManager.reset();
          FlowManager.destroy();
        }
      }
    };
  }

  @NonNull public static DBFlowTestRule create() {
    return new DBFlowTestRule();
  }
}
