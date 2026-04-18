package com.kelsos.mbrc.core.networking

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.networking.data.DeserializationAdapter
import com.kelsos.mbrc.core.networking.data.SerializationAdapter
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class RequestManagerImplTest : KoinTest {

  private val dispatchers: AppCoroutineDispatchers by inject()

  @Before
  fun setUp() {
    startKoin { modules(testDispatcherModule) }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `openConnection throws NoDefaultConnectionException when no default is configured`() =
    runTest(testDispatcher) {
      val connectionProvider = mockk<DefaultConnectionProvider>()
      every { connectionProvider.getDefault() } returns null

      val manager = RequestManagerImpl(
        serializationAdapter = mockk<SerializationAdapter>(relaxed = true),
        deserializationAdapter = mockk<DeserializationAdapter>(relaxed = true),
        clientIdProvider = mockk<ClientIdProvider>(relaxed = true),
        connectionProvider = connectionProvider,
        dispatchers = dispatchers
      )

      val thrown = runCatching { manager.openConnection() }.exceptionOrNull()
      assertThat(thrown).isInstanceOf(NoDefaultConnectionException::class.java)
    }
}
