package com.kelsos.mbrc.rules

import org.junit.runners.model.Statement
import org.mockito.MockitoAnnotations


internal class MockitInitilizationStatement(private val base: Statement, private val test: Any) : Statement() {

  @Throws(Throwable::class)
  override fun evaluate() {
    MockitoAnnotations.initMocks(test)
    base.evaluate()
  }
}
