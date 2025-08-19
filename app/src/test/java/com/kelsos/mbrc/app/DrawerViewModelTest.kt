package com.kelsos.mbrc.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.common.state.ConnectionStateFlow
import com.kelsos.mbrc.common.state.ConnectionStatus
import com.kelsos.mbrc.features.settings.ConnectionRepository
import com.kelsos.mbrc.features.settings.ConnectionSettings
import com.kelsos.mbrc.networking.ClientConnectionUseCase
import com.kelsos.mbrc.utils.testDispatcher
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class DrawerViewModelTest : KoinTest {
  private val testModule = module {
    single<ConnectionStateFlow> { mockk(relaxed = true) }
    single<ClientConnectionUseCase> { mockk(relaxed = true) }
    single<ConnectionRepository> { mockk(relaxed = true) }
    singleOf(::DrawerViewModel)
  }

  private val viewModel: DrawerViewModel by inject()
  private val connectionStateFlow: ConnectionStateFlow by inject()
  private val clientConnectionUseCase: ClientConnectionUseCase by inject()

  private val connectionStatusFlow = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Offline)

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }

    // Setup default mocks
    every { connectionStateFlow.connection } returns connectionStatusFlow
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun `connectionStatus should return current connection state`() {
    runTest(testDispatcher) {
      // Given
      val expectedStatus = ConnectionStatus.Connected
      connectionStatusFlow.value = expectedStatus

      // When
      viewModel.connectionStatus.test {
        // Then
        assertThat(awaitItem()).isEqualTo(expectedStatus)
      }
    }
  }

  @Test
  fun `isConnected should return true when status is Connected`() {
    // Given
    connectionStatusFlow.value = ConnectionStatus.Connected

    // When
    val result = viewModel.isConnected()

    // Then
    assertThat(result).isTrue()
  }

  @Test
  fun `isConnected should return false when status is Offline`() {
    // Given
    connectionStatusFlow.value = ConnectionStatus.Offline

    // When
    val result = viewModel.isConnected()

    // Then
    assertThat(result).isFalse()
  }

  @Test
  fun `isConnected should return false when status is Authenticating`() {
    // Given
    connectionStatusFlow.value = ConnectionStatus.Authenticating

    // When
    val result = viewModel.isConnected()

    // Then
    assertThat(result).isFalse()
  }

  @Test
  fun `toggleConnection should disconnect when currently connected`() {
    runTest(testDispatcher) {
      // Given
      connectionStatusFlow.value = ConnectionStatus.Connected
      coEvery { clientConnectionUseCase.disconnect() } returns Unit

      // When
      viewModel.toggleConnection()

      // Then
      coVerify(exactly = 1) { clientConnectionUseCase.disconnect() }
      coVerify(exactly = 0) { clientConnectionUseCase.connect() }
    }
  }

  @Test
  fun `toggleConnection should connect when currently disconnected`() {
    runTest(testDispatcher) {
      // Given
      connectionStatusFlow.value = ConnectionStatus.Offline
      coEvery { clientConnectionUseCase.connect() } returns Unit

      // When
      viewModel.toggleConnection()

      // Then
      coVerify(exactly = 1) { clientConnectionUseCase.connect() }
      coVerify(exactly = 0) { clientConnectionUseCase.disconnect() }
    }
  }

  @Test
  fun `toggleConnection should connect when currently authenticating`() {
    runTest(testDispatcher) {
      // Given
      connectionStatusFlow.value = ConnectionStatus.Authenticating
      coEvery { clientConnectionUseCase.connect() } returns Unit

      // When
      viewModel.toggleConnection()

      // Then
      coVerify(exactly = 1) { clientConnectionUseCase.connect() }
      coVerify(exactly = 0) { clientConnectionUseCase.disconnect() }
    }
  }

  @Test
  fun `connectionStatus should update when connection state changes`() {
    runTest(testDispatcher) {
      viewModel.connectionStatus.test {
        // Initial state
        assertThat(awaitItem()).isEqualTo(ConnectionStatus.Offline)

        // Change to Authenticating
        connectionStatusFlow.value = ConnectionStatus.Authenticating
        assertThat(awaitItem()).isEqualTo(ConnectionStatus.Authenticating)

        // Change to Connected
        connectionStatusFlow.value = ConnectionStatus.Connected
        assertThat(awaitItem()).isEqualTo(ConnectionStatus.Connected)

        // Change back to Offline
        connectionStatusFlow.value = ConnectionStatus.Offline
        assertThat(awaitItem()).isEqualTo(ConnectionStatus.Offline)
      }
    }
  }

  @Test
  fun `connectionName should return default connection name when connected`() {
    runTest(testDispatcher) {
      // Given
      val expectedName = "Living Room PC"
      val connectionSettings = ConnectionSettings(
        address = "192.168.1.100",
        port = 3000,
        name = expectedName,
        isDefault = true,
        id = 1L
      )

      // Setup mock for ConnectionRepository
      val mockConnectionRepository = mockk<ConnectionRepository>(relaxed = true)
      coEvery { mockConnectionRepository.getDefault() } returns connectionSettings

      val localConnectionStatusFlow = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Offline)

      val testModuleWithName = module {
        single<ConnectionStateFlow> { mockk(relaxed = true) }
        single<ClientConnectionUseCase> { mockk(relaxed = true) }
        single<ConnectionRepository> { mockConnectionRepository }
        singleOf(::DrawerViewModel)
      }

      // Restart Koin with new module
      stopKoin()
      startKoin {
        modules(listOf(testModuleWithName, testDispatcherModule))
      }
      val newConnectionStateFlow: ConnectionStateFlow by inject()
      every { newConnectionStateFlow.connection } returns localConnectionStatusFlow

      val newViewModel: DrawerViewModel by inject()

      // Then
      newViewModel.connectionName.test {
        // Initially null when offline
        assertThat(awaitItem()).isNull()

        // When connected, should load the name
        localConnectionStatusFlow.value = ConnectionStatus.Connected
        assertThat(awaitItem()).isEqualTo(expectedName)
      }
    }
  }

  @Test
  fun `connectionName should return null when not connected`() {
    runTest(testDispatcher) {
      // Given - default mock returns a connection
      val mockConnectionRepository = mockk<ConnectionRepository>(relaxed = true)
      coEvery { mockConnectionRepository.getDefault() } returns ConnectionSettings(
        address = "192.168.1.100",
        port = 3000,
        name = "Test PC",
        isDefault = true,
        id = 1L
      )

      val localConnectionStatusFlow = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Offline)

      val testModuleOffline = module {
        single<ConnectionStateFlow> { mockk(relaxed = true) }
        single<ClientConnectionUseCase> { mockk(relaxed = true) }
        single<ConnectionRepository> { mockConnectionRepository }
        singleOf(::DrawerViewModel)
      }

      // Restart Koin with new module
      stopKoin()
      startKoin {
        modules(listOf(testModuleOffline, testDispatcherModule))
      }
      val newConnectionStateFlow: ConnectionStateFlow by inject()
      every { newConnectionStateFlow.connection } returns localConnectionStatusFlow

      val newViewModel: DrawerViewModel by inject()

      // Then - should be null when offline
      newViewModel.connectionName.test {
        assertThat(awaitItem()).isNull()
      }
    }
  }

  @Test
  fun `connectionName should become null when disconnected`() {
    runTest(testDispatcher) {
      // Given
      val expectedName = "Living Room PC"
      val connectionSettings = ConnectionSettings(
        address = "192.168.1.100",
        port = 3000,
        name = expectedName,
        isDefault = true,
        id = 1L
      )

      val mockConnectionRepository = mockk<ConnectionRepository>(relaxed = true)
      coEvery { mockConnectionRepository.getDefault() } returns connectionSettings

      // Start with Offline status
      val localConnectionStatusFlow = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Offline)

      val testModuleWithName = module {
        single<ConnectionStateFlow> { mockk(relaxed = true) }
        single<ClientConnectionUseCase> { mockk(relaxed = true) }
        single<ConnectionRepository> { mockConnectionRepository }
        singleOf(::DrawerViewModel)
      }

      // Restart Koin with new module
      stopKoin()
      startKoin {
        modules(listOf(testModuleWithName, testDispatcherModule))
      }
      val newConnectionStateFlow: ConnectionStateFlow by inject()
      every { newConnectionStateFlow.connection } returns localConnectionStatusFlow

      val newViewModel: DrawerViewModel by inject()

      // Then
      newViewModel.connectionName.test {
        // Initially null when offline
        assertThat(awaitItem()).isNull()

        // Connect - should load the name
        localConnectionStatusFlow.value = ConnectionStatus.Connected
        assertThat(awaitItem()).isEqualTo(expectedName)

        // When disconnected, should become null
        localConnectionStatusFlow.value = ConnectionStatus.Offline
        assertThat(awaitItem()).isNull()
      }
    }
  }

  @Test
  fun `connectionName should return null when no default connection exists`() {
    runTest(testDispatcher) {
      // Given - setup mock for ConnectionRepository
      val mockConnectionRepository = mockk<ConnectionRepository>(relaxed = true)
      coEvery { mockConnectionRepository.getDefault() } returns null

      val localConnectionStatusFlow = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Connected)

      val testModuleWithNoConnection = module {
        single<ConnectionStateFlow> { mockk(relaxed = true) }
        single<ClientConnectionUseCase> { mockk(relaxed = true) }
        single<ConnectionRepository> { mockConnectionRepository }
        singleOf(::DrawerViewModel)
      }

      // Restart Koin with new module
      stopKoin()
      startKoin {
        modules(listOf(testModuleWithNoConnection, testDispatcherModule))
      }
      val newConnectionStateFlow: ConnectionStateFlow by inject()
      every { newConnectionStateFlow.connection } returns localConnectionStatusFlow

      val newViewModel: DrawerViewModel by inject()

      // Then - even when connected, null if no default exists
      newViewModel.connectionName.test {
        assertThat(awaitItem()).isNull()
      }
    }
  }

  @Test
  fun `connectionName should return address port when connection name is blank`() {
    runTest(testDispatcher) {
      // Given
      val connectionSettings = ConnectionSettings(
        address = "192.168.1.100",
        port = 3000,
        name = "   ",
        isDefault = true,
        id = 1L
      )

      // Setup mock for ConnectionRepository
      val mockConnectionRepository = mockk<ConnectionRepository>(relaxed = true)
      coEvery { mockConnectionRepository.getDefault() } returns connectionSettings

      // Start with Offline status
      val localConnectionStatusFlow = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Offline)

      val testModuleWithBlankName = module {
        single<ConnectionStateFlow> { mockk(relaxed = true) }
        single<ClientConnectionUseCase> { mockk(relaxed = true) }
        single<ConnectionRepository> { mockConnectionRepository }
        singleOf(::DrawerViewModel)
      }

      // Restart Koin with new module
      stopKoin()
      startKoin {
        modules(listOf(testModuleWithBlankName, testDispatcherModule))
      }
      val newConnectionStateFlow: ConnectionStateFlow by inject()
      every { newConnectionStateFlow.connection } returns localConnectionStatusFlow

      val newViewModel: DrawerViewModel by inject()

      // Then - when connected with blank name, should return address:port
      newViewModel.connectionName.test {
        // Initially null when offline
        assertThat(awaitItem()).isNull()

        // Connect - should return address:port since name is blank
        localConnectionStatusFlow.value = ConnectionStatus.Connected
        assertThat(awaitItem()).isEqualTo("192.168.1.100:3000")
      }
    }
  }
}
