package com.kelsos.mbrc.feature.settings.connectlink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import com.kelsos.mbrc.core.networking.connectlink.ConnectLink
import com.kelsos.mbrc.core.networking.connectlink.ConnectLinkError
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConnectLinkDialogTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private val link = ConnectLink(
    hosts = listOf("192.168.1.5", "172.20.48.1"),
    port = 3000,
    name = "LIVING-ROOM-PC",
    serverId = null,
    pairingCode = null
  )

  private val calls = mutableListOf<String>()

  private fun show(state: ConnectLinkState) {
    composeTestRule.setContent {
      ConnectLinkDialog(
        state = state,
        onConnect = { calls += "connect" },
        onGrantLocalNetworkAccess = { calls += "grant" },
        onDismiss = { calls += "dismiss" }
      )
    }
  }

  @Test
  fun `the confirm dialog shows the host and connects only on the tap`() {
    show(ConnectLinkState.Confirm(link, saved = null, problem = null))

    composeTestRule.onNodeWithText("Connect to MusicBee").assertIsDisplayed()
    composeTestRule.onNodeWithText("LIVING-ROOM-PC").assertIsDisplayed()
    composeTestRule.onNodeWithText("192.168.1.5\n172.20.48.1").assertIsDisplayed()
    composeTestRule.onNodeWithText("Port 3000").assertIsDisplayed()
    assertWithMessage("nothing may connect before the tap").that(calls).isEmpty()

    composeTestRule.onNodeWithText("Connect").performClick()

    assertThat(calls).containsExactly("connect")
  }

  @Test
  fun `cancel dismisses without connecting`() {
    show(ConnectLinkState.Confirm(link, saved = null, problem = null))

    composeTestRule.onNodeWithText("Cancel").performClick()

    assertThat(calls).containsExactly("dismiss")
  }

  @Test
  fun `an unanswered attempt offers a retry`() {
    show(ConnectLinkState.Confirm(link, saved = null, problem = ConnectProblem.Unreachable))

    composeTestRule.onNodeWithText("MusicBee did not answer").assertIsDisplayed()
    composeTestRule.onNodeWithText("Try again").performClick()

    assertThat(calls).containsExactly("connect")
  }

  @Test
  fun `denied local network access sends the user to grant it instead of connecting`() {
    show(ConnectLinkState.Confirm(link, saved = null, problem = ConnectProblem.LocalNetworkDenied))

    composeTestRule.onNodeWithText("Local network access is off").assertIsDisplayed()
    composeTestRule.onNodeWithText("Grant access").performClick()

    assertThat(calls).containsExactly("grant")
  }

  @Test
  fun `a failed save offers a retry`() {
    show(ConnectLinkState.Confirm(link, saved = null, problem = ConnectProblem.SaveFailed))

    composeTestRule.onNodeWithText("Couldn't add this computer").assertIsDisplayed()
    composeTestRule.onNodeWithText("Try again").performClick()

    assertThat(calls).containsExactly("connect")
  }

  @Test
  fun `connecting shows progress and can be cancelled`() {
    show(ConnectLinkState.Connecting)

    composeTestRule.onNodeWithText("Looking for MusicBee…").assertIsDisplayed()
    composeTestRule.onNodeWithText("Cancel").performClick()

    assertThat(calls).containsExactly("dismiss")
  }

  @Test
  fun `each refused link explains itself`() {
    val messages = mapOf(
      ConnectLinkError.NotAConnectLink to "This is not a MusicBee Remote connection link.",
      ConnectLinkError.NewerVersion to
        "This link comes from a newer plugin. Update MusicBee Remote to use it.",
      ConnectLinkError.Malformed to
        "This link is incomplete or damaged. Show the QR code again in the plugin and scan it again.",
      ConnectLinkError.NoLocalAddress to
        "This link points outside your local network, so it was not used."
    )
    var state: ConnectLinkState by mutableStateOf(
      ConnectLinkState.Rejected(ConnectLinkError.NotAConnectLink)
    )
    composeTestRule.setContent {
      ConnectLinkDialog(
        state = state,
        onConnect = { calls += "connect" },
        onGrantLocalNetworkAccess = { calls += "grant" },
        onDismiss = { calls += "dismiss" }
      )
    }

    messages.forEach { (error, message) ->
      state = ConnectLinkState.Rejected(error)
      composeTestRule.onNodeWithText(message).assertIsDisplayed()
    }
    composeTestRule.onNodeWithText("OK").performClick()

    assertThat(calls).containsExactly("dismiss")
  }

  @Test
  fun `a saved computer is offered as the default under its own name`() {
    val saved = ConnectionSettings("192.168.1.5", 3000, "Study", isDefault = false, id = 3)
    show(ConnectLinkState.Confirm(link, saved = saved, problem = null))

    composeTestRule
      .onNodeWithText("Study is already saved. Make it the default and connect to it?")
      .assertIsDisplayed()
    composeTestRule.onNodeWithText("LIVING-ROOM-PC").assertDoesNotExist()
    composeTestRule.onNodeWithText("Connect").performClick()

    assertThat(calls).containsExactly("connect")
  }

  @Test
  fun `an already connected computer only needs acknowledging`() {
    show(ConnectLinkState.AlreadyConnected("Study"))

    composeTestRule.onNodeWithText("Already connected").assertIsDisplayed()
    composeTestRule.onNodeWithText(
      "MusicBee Remote is already connected to Study."
    ).assertIsDisplayed()
    composeTestRule.onNodeWithText("Connect").assertDoesNotExist()
    composeTestRule.onNodeWithText("OK").performClick()

    assertThat(calls).containsExactly("dismiss")
  }

  @Test
  fun `the hidden state draws nothing`() {
    show(ConnectLinkState.Hidden)

    composeTestRule.onNodeWithText("Connect to MusicBee").assertDoesNotExist()
  }
}
