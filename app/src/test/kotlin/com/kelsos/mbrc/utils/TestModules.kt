package com.kelsos.mbrc.utils

// Re-export shared test utilities from core:common testFixtures
import com.kelsos.mbrc.core.common.test.parserModule as coreParserModule
import com.kelsos.mbrc.core.common.test.testDispatcher as coreTestDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule as coreTestDispatcherModule
import com.kelsos.mbrc.core.common.test.testDispatchers as coreTestDispatchers

val testDispatcher = coreTestDispatcher
val testDispatchers = coreTestDispatchers
val testDispatcherModule = coreTestDispatcherModule
val parserModule = coreParserModule
