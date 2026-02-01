package com.kelsos.mbrc.core.networking

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.state.ConnectionStatePublisher
import com.kelsos.mbrc.core.common.state.ConnectionStatus
import com.kelsos.mbrc.core.common.test.testDispatcher
import com.kelsos.mbrc.core.common.test.testDispatcherModule
import com.kelsos.mbrc.core.networking.client.MessageQueue
import com.kelsos.mbrc.core.networking.client.SocketMessage
import com.kelsos.mbrc.core.networking.client.UiMessage
import com.kelsos.mbrc.core.networking.client.UiMessageQueue
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.base.ProtocolAction
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class MessageHandlerTest : KoinTest {

  private lateinit var messageHandler: MessageHandler
  private lateinit var actionFactory: ProtocolActionFactory
  private lateinit var messageQueue: MessageQueue
  private lateinit var uiMessageQueue: UiMessageQueue
  private lateinit var connectionState: ConnectionStatePublisher
  private lateinit var clientIdProvider: ClientIdProvider
  private lateinit var librarySyncTrigger: LibrarySyncTrigger

  private val connectionFlow = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Offline)
  private val messagesFlow = MutableSharedFlow<SocketMessage>()
  private val uiMessagesFlow = MutableSharedFlow<UiMessage>()
  private val mockAction: ProtocolAction = mockk(relaxed = true)

  private val testModule = module {
    single { actionFactory }
    single { messageQueue }
    single { uiMessageQueue }
    single { connectionState }
    single { clientIdProvider }
    single { librarySyncTrigger }
  }

  @Before
  fun setUp() {
    actionFactory = mockk {
      every { create(any()) } returns mockAction
    }
    messageQueue = mockk {
      coEvery { queue(any()) } just Runs
      every { messages } returns messagesFlow
    }
    uiMessageQueue = mockk {
      every { messages } returns uiMessagesFlow
      coEvery { messages.emit(any()) } just Runs
    }
    connectionState = mockk {
      every { connection } returns connectionFlow
      coEvery { updateConnection(any()) } just Runs
    }
    clientIdProvider = mockk {
      coEvery { getClientId() } returns "test-client-id"
    }
    librarySyncTrigger = mockk {
      coEvery { sync(any()) } just Runs
    }

    startKoin { modules(listOf(testModule, testDispatcherModule)) }

    messageHandler = MessageHandlerImpl(
      actionFactory = actionFactory,
      messageQueue = messageQueue,
      uiMessageQueue = uiMessageQueue,
      connectionState = connectionState,
      clientIdProvider = clientIdProvider,
      librarySyncTrigger = librarySyncTrigger,
      dispatchers = org.koin.java.KoinJavaComponent.get(
        com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers::class.java
      )
    )
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  // region Message validation tests

  @Test
  fun `processIncoming should reject message with blank context`() = runTest(testDispatcher) {
    val message = SocketMessage(context = "", data = "test")

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    coVerify(exactly = 0) { actionFactory.create(any()) }
  }

  @Test
  fun `processIncoming should reject message with context longer than 100 chars`() =
    runTest(testDispatcher) {
      val longContext = "a".repeat(101)
      val message = SocketMessage(context = longContext, data = "test")

      messageHandler.processIncoming(message)
      advanceUntilIdle()

      coVerify(exactly = 0) { actionFactory.create(any()) }
    }

  @Test
  fun `processIncoming should accept message with context at max length`() =
    runTest(testDispatcher) {
      connectionFlow.value = ConnectionStatus.Connected
      val maxContext = "a".repeat(100)
      val message = SocketMessage(context = maxContext, data = "test")

      messageHandler.processIncoming(message)
      advanceUntilIdle()

      // Unknown command - no action executed, but message was processed
    }

  @Test
  fun `processIncoming should reject message with data longer than 10000 chars`() =
    runTest(testDispatcher) {
      val longData = "a".repeat(10001)
      val message = SocketMessage(context = "test", data = longData)

      messageHandler.processIncoming(message)
      advanceUntilIdle()

      coVerify(exactly = 0) { actionFactory.create(any()) }
    }

  @Test
  fun `processIncoming should accept message with data at max length`() = runTest(testDispatcher) {
    connectionFlow.value = ConnectionStatus.Connected
    val maxData = "a".repeat(10000)
    val message = SocketMessage(context = Protocol.NowPlayingTrack.context, data = maxData)

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    coVerify { actionFactory.create(Protocol.NowPlayingTrack) }
  }

  // endregion

  // region Control protocol tests

  @Test
  fun `processIncoming should emit NotAllowed and go offline on ClientNotAllowed`() =
    runTest(testDispatcher) {
      val message = SocketMessage.create(Protocol.ClientNotAllowed)

      messageHandler.processIncoming(message)
      advanceUntilIdle()

      coVerify { uiMessageQueue.messages.emit(UiMessage.NotAllowed) }
      coVerify { connectionState.updateConnection(ConnectionStatus.Offline) }
    }

  @Test
  fun `processIncoming should emit PartyModeCommandUnavailable on CommandUnavailable`() =
    runTest(testDispatcher) {
      val message = SocketMessage.create(Protocol.CommandUnavailable)

      messageHandler.processIncoming(message)
      advanceUntilIdle()

      coVerify { uiMessageQueue.messages.emit(UiMessage.PartyModeCommandUnavailable) }
    }

  @Test
  fun `processIncoming should do nothing on UnknownCommand`() = runTest(testDispatcher) {
    val message = SocketMessage.create(Protocol.UnknownCommand)

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    coVerify(exactly = 0) { actionFactory.create(any()) }
    coVerify(exactly = 0) { connectionState.updateConnection(any()) }
  }

  // endregion

  // region Handshake tests

  @Test
  fun `processIncoming should send protocol payload on Player message`() = runTest(testDispatcher) {
    val message = SocketMessage.create(Protocol.Player)

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    val slot = slot<SocketMessage>()
    coVerify { messageQueue.queue(capture(slot)) }
    assertThat(slot.captured.context).isEqualTo(Protocol.ProtocolTag.context)
  }

  @Test
  fun `processIncoming should connect on valid ProtocolTag response`() = runTest(testDispatcher) {
    val message = SocketMessage(context = Protocol.ProtocolTag.context, data = "4")

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    coVerify { connectionState.updateConnection(ConnectionStatus.Connected) }
    coVerify { messageQueue.queue(match { it.context == Protocol.Init.context }) }
    coVerify { librarySyncTrigger.sync(auto = true) }
  }

  @Test
  fun `processIncoming should accept minimum supported protocol version 2`() =
    runTest(testDispatcher) {
      val message = SocketMessage(context = Protocol.ProtocolTag.context, data = "2")

      messageHandler.processIncoming(message)
      advanceUntilIdle()

      coVerify { connectionState.updateConnection(ConnectionStatus.Connected) }
    }

  @Test
  fun `processIncoming should accept current protocol version 4`() = runTest(testDispatcher) {
    val message = SocketMessage(context = Protocol.ProtocolTag.context, data = "4")

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    coVerify { connectionState.updateConnection(ConnectionStatus.Connected) }
  }

  @Test
  fun `processIncoming should reject protocol version 1`() = runTest(testDispatcher) {
    val message = SocketMessage(context = Protocol.ProtocolTag.context, data = "1")

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    coVerify { uiMessageQueue.messages.emit(UiMessage.ConnectionError.UnsupportedProtocolVersion) }
    coVerify { connectionState.updateConnection(ConnectionStatus.Offline) }
  }

  @Test
  fun `processIncoming should reject protocol version higher than supported`() =
    runTest(testDispatcher) {
      val message = SocketMessage(context = Protocol.ProtocolTag.context, data = "5")

      messageHandler.processIncoming(message)
      advanceUntilIdle()

      coVerify {
        uiMessageQueue.messages.emit(UiMessage.ConnectionError.UnsupportedProtocolVersion)
      }
      coVerify { connectionState.updateConnection(ConnectionStatus.Offline) }
    }

  @Test
  fun `processIncoming should use minimum version for empty protocol version`() =
    runTest(testDispatcher) {
      val message = SocketMessage(context = Protocol.ProtocolTag.context, data = "")

      messageHandler.processIncoming(message)
      advanceUntilIdle()

      // Empty string defaults to MIN_SUPPORTED_VERSION (2), which is valid
      coVerify { connectionState.updateConnection(ConnectionStatus.Connected) }
    }

  @Test
  fun `processIncoming should use minimum version for invalid protocol version`() =
    runTest(testDispatcher) {
      val message = SocketMessage(context = Protocol.ProtocolTag.context, data = "not-a-number")

      messageHandler.processIncoming(message)
      advanceUntilIdle()

      // Invalid string defaults to MIN_SUPPORTED_VERSION (2), which is valid
      coVerify { connectionState.updateConnection(ConnectionStatus.Connected) }
    }

  @Test
  fun `processIncoming should parse float protocol version as int`() = runTest(testDispatcher) {
    val message = SocketMessage(context = Protocol.ProtocolTag.context, data = "3.5")

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    // 3.5 -> 3, which is valid
    coVerify { connectionState.updateConnection(ConnectionStatus.Connected) }
  }

  // endregion

  // region Command execution tests

  @Test
  fun `processIncoming should execute command when connected`() = runTest(testDispatcher) {
    connectionFlow.value = ConnectionStatus.Connected
    val message = SocketMessage.create(Protocol.NowPlayingTrack, mapOf("title" to "Test"))

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    coVerify { actionFactory.create(Protocol.NowPlayingTrack) }
    coVerify { mockAction.execute(any()) }
  }

  @Test
  fun `processIncoming should not execute command when offline`() = runTest(testDispatcher) {
    connectionFlow.value = ConnectionStatus.Offline
    val message = SocketMessage.create(Protocol.NowPlayingTrack, mapOf("title" to "Test"))

    messageHandler.processIncoming(message)
    advanceUntilIdle()

    coVerify(exactly = 0) { mockAction.execute(any()) }
  }

  // endregion

  // region startHandshake tests

  @Test
  fun `startHandshake should queue player message`() = runTest(testDispatcher) {
    messageHandler.startHandshake()
    advanceUntilIdle()

    val slot = slot<SocketMessage>()
    coVerify { messageQueue.queue(capture(slot)) }
    assertThat(slot.captured.context).isEqualTo(Protocol.Player.context)
    assertThat(slot.captured.data).isEqualTo("Android")
  }

  // endregion
}
