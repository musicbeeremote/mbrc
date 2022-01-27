package com.kelsos.mbrc.rules

import com.kelsos.mbrc.utils.mainTestDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class CoroutineTestRule : TestWatcher() {
  override fun starting(description: Description) {
    super.starting(description)
    Dispatchers.setMain(mainTestDispatcher)
  }

  override fun finished(description: Description) {
    super.finished(description)
    Dispatchers.resetMain()
  }
}
