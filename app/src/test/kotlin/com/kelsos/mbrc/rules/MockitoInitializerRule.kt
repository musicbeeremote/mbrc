package com.kelsos.mbrc.rules

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockitoInitializerRule(private val test: Any) : TestRule {

  override fun apply(base: Statement, description: Description): Statement {
    return MockitInitilizationStatement(base, test)
  }
}