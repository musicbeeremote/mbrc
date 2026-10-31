package com.kelsos.mbrc.feature.settings.connectlink

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.state.ConnectionStatus
import com.kelsos.mbrc.core.networking.client.UiMessage
import com.kelsos.mbrc.core.networking.client.UiMessageQueueImpl
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkError
import com.kelsos.mbrc.core.networking.connectlink.PluginProbe
import com.kelsos.mbrc.core.networking.connectlink.ReachableHostFinder
import com.kelsos.mbrc.feature.settings.domain.ConnectionRepository
import com.kelsos.mbrc.feature.settings.domain.DefaultConnectionSwitcher
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectLinkViewModelTest {

  /** One recorded call to the address form of [DefaultConnectionSwitcher.switchTo]. */
  private data class Switch(
    val address: String,
    val port: Int,
    val name: String,
    val sameHostAddresses: List<String>
  )

  /** Records each switch and answers with the stored host, or throws when [failing] is set. */
  private class RecordingSwitcher : DefaultConnectionSwitcher {
    val switches = mutableListOf<Switch>()
    var failing = false

    override suspend fun switchTo(connection: ConnectionSettings) = Unit

    override suspend fun switchTo(
      address: String,
      port: Int,
      name: String,
      sameHostAddresses: List<String>,
      onSaved: suspend (ConnectionSettings) -> Unit
    ) {
      check(!failing) { "database unavailable" }
      switches += Switch(address, port, name, sameHostAddresses)
      onSaved(ConnectionSettings(address, port, name, isDefault = true, id = 7))
    }
  }

  private class FakeConnectionState : ConnectionStateFlow {
    override val connection: StateFlow<ConnectionStatus> =
      MutableStateFlow(ConnectionStatus.Offline)
    override var isConnected = false
  }

  private val dispatcher = StandardTestDispatcher()
  private val switcher = RecordingSwitcher()
  private val connectionState = FakeConnectionState()
  private val uiMessageQueue = UiMessageQueueImpl()
  private val savedState = SavedStateHandle()
  private val repository = mockk<ConnectionRepository>()
  private var savedHost: ConnectionSettings? = null
  private var answering = setOf<String>()
  private var localNetworkPermitted = true

  private val probe = PluginProbe { address, _ -> address in answering }

  private val twoHostLink =
    "https://mbrc.kelsos.net/connect#v=1&h=192.168.1.5,172.20.48.1&p=3000&n=Den%20PC"

  private lateinit var viewModel: ConnectLinkViewModel

  private fun viewModelWith(
    finder: ReachableHostFinder = ReachableHostFinder(probe),
    state: SavedStateHandle = savedState
  ) = ConnectLinkViewModel(
    state,
    repository,
    connectionState,
    finder,
    switcher,
    { localNetworkPermitted },
    uiMessageQueue
  )

  private fun TestScope.open(link: String) {
    viewModel.open(link)
    advanceUntilIdle()
  }

  private fun confirm() = viewModel.state.value as ConnectLinkState.Confirm

  @Before
  fun setUp() {
    Dispatchers.setMain(dispatcher)
    coEvery { repository.findSaved(any(), any()) } answers { savedHost }
    viewModel = viewModelWith()
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `opening a link for a new computer asks to add it and does not connect`() =
    runTest(dispatcher) {
      answering = setOf("192.168.1.5")

      open(twoHostLink)

      assertThat(confirm().saved).isNull()
      assertThat(confirm().problem).isNull()
      assertThat(switcher.switches).isEmpty()
    }

  @Test
  fun `opening a refused link reports why`() = runTest(dispatcher) {
    open("https://mbrc.kelsos.net/connect#v=1&h=8.8.8.8&p=3000")

    assertThat(viewModel.state.value)
      .isEqualTo(ConnectLinkState.Rejected(ConnectLinkError.NoLocalAddress))
  }

  @Test
  fun `a computer saved at one of the link's addresses is offered as saved`() =
    runTest(dispatcher) {
      savedHost = ConnectionSettings("172.20.48.1", 3000, "Study", isDefault = false, id = 3)

      open(twoHostLink)

      assertThat(confirm().saved).isEqualTo(savedHost)
    }

  @Test
  fun `the connected default computer needs no switch`() = runTest(dispatcher) {
    savedHost = ConnectionSettings("192.168.1.5", 3000, "Study", isDefault = true, id = 3)
    connectionState.isConnected = true

    open(twoHostLink)

    assertWithMessage("rescanning the host already in use must not drop a working connection")
      .that(viewModel.state.value)
      .isEqualTo(ConnectLinkState.AlreadyConnected("Study"))
  }

  @Test
  fun `the default computer is offered again while it is not connected`() = runTest(dispatcher) {
    savedHost = ConnectionSettings("192.168.1.5", 3000, "Study", isDefault = true, id = 3)
    connectionState.isConnected = false

    open(twoHostLink)

    assertThat(confirm().saved).isEqualTo(savedHost)
  }

  @Test
  fun `confirming switches to the reachable address and announces it`() = runTest(dispatcher) {
    answering = setOf("172.20.48.1")
    open(twoHostLink)

    uiMessageQueue.messages.test {
      viewModel.connect()
      advanceUntilIdle()

      assertThat((awaitItem() as UiMessage.ConnectingTo).name).isEqualTo("Den PC")
    }

    assertWithMessage("every link address is passed on, so a saved row at another one is moved")
      .that(switcher.switches)
      .containsExactly(Switch("172.20.48.1", 3000, "Den PC", listOf("192.168.1.5", "172.20.48.1")))
    assertThat(viewModel.state.value).isEqualTo(ConnectLinkState.Hidden)
  }

  @Test
  fun `a saved computer keeps its own name`() = runTest(dispatcher) {
    savedHost = ConnectionSettings("172.20.48.1", 3000, "Study", isDefault = false, id = 3)
    answering = setOf("192.168.1.5")
    open(twoHostLink)

    viewModel.connect()
    advanceUntilIdle()

    assertThat(switcher.switches.single().name).isEqualTo("Study")
  }

  @Test
  fun `a link without a name is saved under its address`() = runTest(dispatcher) {
    answering = setOf("10.0.0.3")
    open("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=3000")

    viewModel.connect()
    advanceUntilIdle()

    assertThat(switcher.switches.single().name).isEqualTo("10.0.0.3")
  }

  @Test
  fun `no answering address offers a retry and saves nothing`() = runTest(dispatcher) {
    open(twoHostLink)

    viewModel.connect()
    advanceUntilIdle()

    assertThat(confirm().problem).isEqualTo(ConnectProblem.Unreachable)
    assertThat(switcher.switches).isEmpty()

    answering = setOf("192.168.1.5")
    viewModel.connect()
    advanceUntilIdle()

    assertThat(switcher.switches.single().address).isEqualTo("192.168.1.5")
  }

  @Test
  fun `denied local network access asks for the permission instead of probing`() =
    runTest(dispatcher) {
      localNetworkPermitted = false
      answering = setOf("192.168.1.5")
      open(twoHostLink)

      viewModel.connect()
      advanceUntilIdle()

      assertWithMessage("a refused socket must not be reported as MusicBee not answering")
        .that(confirm().problem)
        .isEqualTo(ConnectProblem.LocalNetworkDenied)
      assertThat(switcher.switches).isEmpty()
    }

  @Test
  fun `a grant clears the local network problem so the dialog offers connect`() =
    runTest(dispatcher) {
      localNetworkPermitted = false
      open(twoHostLink)
      viewModel.connect()

      viewModel.refreshLocalNetworkAccess()
      assertWithMessage("still denied, so the problem stays")
        .that(confirm().problem)
        .isEqualTo(ConnectProblem.LocalNetworkDenied)

      localNetworkPermitted = true
      viewModel.refreshLocalNetworkAccess()

      assertThat(confirm().problem).isNull()
    }

  @Test
  fun `a failed save is reported instead of crashing`() = runTest(dispatcher) {
    answering = setOf("192.168.1.5")
    switcher.failing = true
    open(twoHostLink)

    viewModel.connect()
    advanceUntilIdle()

    assertThat(confirm().problem).isEqualTo(ConnectProblem.SaveFailed)
  }

  @Test
  fun `connect does nothing without a confirmed link`() = runTest(dispatcher) {
    open("https://mbrc.kelsos.net/connect#v=2&h=10.0.0.3&p=3000")

    viewModel.connect()
    advanceUntilIdle()

    assertThat(viewModel.state.value)
      .isEqualTo(ConnectLinkState.Rejected(ConnectLinkError.NewerVersion))
    assertThat(switcher.switches).isEmpty()
  }

  @Test
  fun `dismissing while connecting stops the attempt`() = runTest(dispatcher) {
    viewModel = viewModelWith(finder = ReachableHostFinder { _, _ -> awaitCancellation() })
    open(twoHostLink)
    viewModel.connect()
    advanceUntilIdle()
    assertThat(viewModel.state.value).isEqualTo(ConnectLinkState.Connecting)

    viewModel.dismiss()
    advanceUntilIdle()

    assertThat(viewModel.state.value).isEqualTo(ConnectLinkState.Hidden)
    assertThat(switcher.switches).isEmpty()
  }

  @Test
  fun `a pending link survives the process being killed`() = runTest(dispatcher) {
    open(twoHostLink)
    val afterProcessDeath =
      SavedStateHandle(savedState.keys().associateWith { savedState.get<Any>(it) })

    val restored = viewModelWith(state = afterProcessDeath)
    advanceUntilIdle()

    assertThat(restored.state.value).isInstanceOf(ConnectLinkState.Confirm::class.java)
  }

  @Test
  fun `a used or dismissed link is not restored`() = runTest(dispatcher) {
    answering = setOf("192.168.1.5")
    open(twoHostLink)
    viewModel.connect()
    advanceUntilIdle()

    assertWithMessage("after a successful switch")
      .that(viewModelWith().state.value)
      .isEqualTo(ConnectLinkState.Hidden)

    open(twoHostLink)
    viewModel.dismiss()

    assertWithMessage("after a dismiss")
      .that(viewModelWith().state.value)
      .isEqualTo(ConnectLinkState.Hidden)
  }
}
