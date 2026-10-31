package com.kelsos.mbrc.feature.settings.connectlink

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.networking.client.UiMessage
import com.kelsos.mbrc.core.networking.client.UiMessageQueueImpl
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkError
import com.kelsos.mbrc.core.networking.connectlink.PluginProbe
import com.kelsos.mbrc.core.networking.connectlink.ReachableHostFinder
import com.kelsos.mbrc.feature.settings.domain.DefaultConnectionSwitcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectLinkViewModelTest {

  /** Records each switch and answers with the stored host, or throws when [failing] is set. */
  private class RecordingSwitcher : DefaultConnectionSwitcher {
    val switches = mutableListOf<Triple<String, Int, String>>()
    var failing = false

    override suspend fun switchTo(connection: ConnectionSettings) = Unit

    override suspend fun switchTo(
      address: String,
      port: Int,
      name: String,
      onSaved: suspend (ConnectionSettings) -> Unit
    ) {
      check(!failing) { "database unavailable" }
      switches += Triple(address, port, name)
      onSaved(ConnectionSettings(address, port, name, isDefault = true, id = 7))
    }
  }

  private val dispatcher = StandardTestDispatcher()
  private val switcher = RecordingSwitcher()
  private val uiMessageQueue = UiMessageQueueImpl()
  private val savedState = SavedStateHandle()
  private var answering = setOf<String>()
  private var localNetworkPermitted = true

  private val probe = PluginProbe { address, _ -> address in answering }

  private val twoHostLink =
    "https://mbrc.kelsos.net/connect#v=1&h=172.20.48.1,192.168.1.5&p=3000&n=Den%20PC"

  private lateinit var viewModel: ConnectLinkViewModel

  private fun viewModelWith(
    finder: ReachableHostFinder = ReachableHostFinder(probe),
    state: SavedStateHandle = savedState
  ) = ConnectLinkViewModel(
    state,
    finder,
    switcher,
    { localNetworkPermitted },
    uiMessageQueue
  )

  @Before
  fun setUp() {
    Dispatchers.setMain(dispatcher)
    viewModel = viewModelWith()
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `opening a valid link asks for confirmation and does not connect`() = runTest(dispatcher) {
    answering = setOf("192.168.1.5")

    viewModel.open(twoHostLink)
    advanceUntilIdle()

    val confirm = viewModel.state.value as ConnectLinkState.Confirm
    assertThat(confirm.problem).isNull()
    assertThat(switcher.switches).isEmpty()
  }

  @Test
  fun `opening a refused link reports why`() = runTest(dispatcher) {
    viewModel.open("https://mbrc.kelsos.net/connect#v=1&h=8.8.8.8&p=3000")

    assertThat(viewModel.state.value)
      .isEqualTo(ConnectLinkState.Rejected(ConnectLinkError.NoLocalAddress))
  }

  @Test
  fun `confirming switches to the reachable address and announces it`() = runTest(dispatcher) {
    answering = setOf("192.168.1.5")
    viewModel.open(twoHostLink)

    uiMessageQueue.messages.test {
      viewModel.connect()
      advanceUntilIdle()

      val message = awaitItem() as UiMessage.ConnectingTo
      assertThat(message.name).isEqualTo("Den PC")
    }

    assertWithMessage("the WSL-style address listed first did not answer, so the LAN one is saved")
      .that(switcher.switches)
      .containsExactly(Triple("192.168.1.5", 3000, "Den PC"))
    assertThat(viewModel.state.value).isEqualTo(ConnectLinkState.Hidden)
  }

  @Test
  fun `a link without a name is saved under its address`() = runTest(dispatcher) {
    answering = setOf("10.0.0.3")
    viewModel.open("https://mbrc.kelsos.net/connect#v=1&h=10.0.0.3&p=3000")

    viewModel.connect()
    advanceUntilIdle()

    assertThat(switcher.switches).containsExactly(Triple("10.0.0.3", 3000, "10.0.0.3"))
  }

  @Test
  fun `no answering address offers a retry and saves nothing`() = runTest(dispatcher) {
    viewModel.open(twoHostLink)

    viewModel.connect()
    advanceUntilIdle()

    assertThat((viewModel.state.value as ConnectLinkState.Confirm).problem)
      .isEqualTo(ConnectProblem.Unreachable)
    assertThat(switcher.switches).isEmpty()

    answering = setOf("192.168.1.5")
    viewModel.connect()
    advanceUntilIdle()

    assertThat(switcher.switches.single().first).isEqualTo("192.168.1.5")
  }

  @Test
  fun `denied local network access asks for the permission instead of probing`() =
    runTest(dispatcher) {
      localNetworkPermitted = false
      answering = setOf("192.168.1.5")
      viewModel.open(twoHostLink)

      viewModel.connect()
      advanceUntilIdle()

      assertWithMessage("a refused socket must not be reported as MusicBee not answering")
        .that((viewModel.state.value as ConnectLinkState.Confirm).problem)
        .isEqualTo(ConnectProblem.LocalNetworkDenied)
      assertThat(switcher.switches).isEmpty()
    }

  @Test
  fun `a grant clears the local network problem so the dialog offers connect`() =
    runTest(dispatcher) {
      localNetworkPermitted = false
      viewModel.open(twoHostLink)
      viewModel.connect()

      viewModel.refreshLocalNetworkAccess()
      assertWithMessage("still denied, so the problem stays")
        .that((viewModel.state.value as ConnectLinkState.Confirm).problem)
        .isEqualTo(ConnectProblem.LocalNetworkDenied)

      localNetworkPermitted = true
      viewModel.refreshLocalNetworkAccess()

      assertThat((viewModel.state.value as ConnectLinkState.Confirm).problem).isNull()
    }

  @Test
  fun `a failed save is reported instead of crashing`() = runTest(dispatcher) {
    answering = setOf("192.168.1.5")
    switcher.failing = true
    viewModel.open(twoHostLink)

    viewModel.connect()
    advanceUntilIdle()

    assertThat((viewModel.state.value as ConnectLinkState.Confirm).problem)
      .isEqualTo(ConnectProblem.SaveFailed)
  }

  @Test
  fun `connect does nothing without a confirmed link`() = runTest(dispatcher) {
    viewModel.open("https://mbrc.kelsos.net/connect#v=2&h=10.0.0.3&p=3000")

    viewModel.connect()
    advanceUntilIdle()

    assertThat(viewModel.state.value)
      .isEqualTo(ConnectLinkState.Rejected(ConnectLinkError.NewerVersion))
    assertThat(switcher.switches).isEmpty()
  }

  @Test
  fun `dismissing while connecting stops the attempt`() = runTest(dispatcher) {
    viewModel = viewModelWith(finder = ReachableHostFinder { _, _ -> awaitCancellation() })
    viewModel.open(twoHostLink)
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
    viewModel.open(twoHostLink)

    val restored =
      viewModelWith(
        state = SavedStateHandle(
          savedState.keys().associateWith {
            savedState.get<Any>(it)
          }
        )
      )

    assertThat(restored.state.value).isInstanceOf(ConnectLinkState.Confirm::class.java)
  }

  @Test
  fun `a used or dismissed link is not restored`() = runTest(dispatcher) {
    answering = setOf("192.168.1.5")
    viewModel.open(twoHostLink)
    viewModel.connect()
    advanceUntilIdle()

    assertWithMessage("after a successful switch")
      .that(viewModelWith().state.value)
      .isEqualTo(ConnectLinkState.Hidden)

    viewModel.open(twoHostLink)
    viewModel.dismiss()

    assertWithMessage(
      "after a dismiss"
    ).that(viewModelWith().state.value).isEqualTo(ConnectLinkState.Hidden)
  }
}
