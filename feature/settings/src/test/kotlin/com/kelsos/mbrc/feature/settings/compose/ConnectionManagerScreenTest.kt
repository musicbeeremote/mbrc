package com.kelsos.mbrc.feature.settings.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.data.ConnectionSettings
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class ConnectionManagerScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  // region Empty State Tests

  @Test
  fun `empty state displays no connections message`() {
    composeTestRule.setContent {
      EmptyConnectionsState()
    }

    composeTestRule.onNodeWithText("No connections configured").assertIsDisplayed()
  }

  @Test
  fun `empty state displays hint to add connection`() {
    composeTestRule.setContent {
      EmptyConnectionsState()
    }

    composeTestRule.onNodeWithText(
      "Add a connection manually",
      substring = true
    ).assertIsDisplayed()
  }

  // endregion

  // region Connection Item Tests

  @Test
  fun `connection item displays name when provided`() {
    val connection = ConnectionSettings(
      id = 1,
      name = "Home Server",
      address = "192.168.1.100",
      port = 3000,
      isDefault = false
    )

    composeTestRule.setContent {
      ConnectionItemContent(
        connection = connection,
        onEdit = {},
        onSetDefault = {}
      )
    }

    composeTestRule.onNodeWithText("Home Server").assertIsDisplayed()
  }

  @Test
  fun `connection item displays address when name is blank`() {
    val connection = ConnectionSettings(
      id = 1,
      name = "",
      address = "192.168.1.100",
      port = 3000,
      isDefault = false
    )

    composeTestRule.setContent {
      ConnectionItemContent(
        connection = connection,
        onEdit = {},
        onSetDefault = {}
      )
    }

    // When name is blank, address is shown as headline
    composeTestRule.onNodeWithText("192.168.1.100").assertIsDisplayed()
  }

  @Test
  fun `connection item displays address and port in subtitle`() {
    val connection = ConnectionSettings(
      id = 1,
      name = "Home Server",
      address = "192.168.1.100",
      port = 3000,
      isDefault = false
    )

    composeTestRule.setContent {
      ConnectionItemContent(
        connection = connection,
        onEdit = {},
        onSetDefault = {}
      )
    }

    composeTestRule.onNodeWithText("192.168.1.100:3000").assertIsDisplayed()
  }

  @Test
  fun `connection item edit button triggers onEdit`() {
    var editClicked = false
    val connection = ConnectionSettings(
      id = 1,
      name = "Home Server",
      address = "192.168.1.100",
      port = 3000,
      isDefault = false
    )

    composeTestRule.setContent {
      ConnectionItemContent(
        connection = connection,
        onEdit = { editClicked = true },
        onSetDefault = {}
      )
    }

    // Click the edit button by content description
    composeTestRule.onNodeWithContentDescription("Edit").performClick()
    assertThat(editClicked).isTrue()
  }

  @Test
  fun `connection item click triggers onSetDefault`() {
    var setDefaultClicked = false
    val connection = ConnectionSettings(
      id = 1,
      name = "Home Server",
      address = "192.168.1.100",
      port = 3000,
      isDefault = false
    )

    composeTestRule.setContent {
      ConnectionItemContent(
        connection = connection,
        onEdit = {},
        onSetDefault = { setDefaultClicked = true }
      )
    }

    composeTestRule.onNodeWithText("Home Server").performClick()
    assertThat(setDefaultClicked).isTrue()
  }

  // endregion

  // region Connection Status Indicator Tests

  @Test
  fun `status indicator shows star icon for default connection`() {
    composeTestRule.setContent {
      ConnectionStatusIndicator(isDefault = true)
    }

    // Default connection shows star icon with content description
    composeTestRule.onNodeWithContentDescription("Default connection").assertIsDisplayed()
  }

  @Test
  fun `status indicator renders for non-default connection`() {
    composeTestRule.setContent {
      ConnectionStatusIndicator(isDefault = false)
    }

    // Non-default connection shows computer icon (no specific content description)
    // Verify the component renders without throwing
    // The computer icon has no content description for non-default connections
  }

  // endregion

  // region Scanning Overlay Tests

  @Test
  fun `scanning overlay displays scanning message`() {
    composeTestRule.setContent {
      ScanningOverlay(onCancel = {})
    }

    composeTestRule.onNodeWithText("Scanning for MusicBee", substring = true).assertIsDisplayed()
  }

  @Test
  fun `scanning overlay cancel button triggers onCancel`() {
    var cancelClicked = false

    composeTestRule.setContent {
      ScanningOverlay(onCancel = { cancelClicked = true })
    }

    composeTestRule.onNodeWithText("Cancel").performClick()
    assertThat(cancelClicked).isTrue()
  }

  // endregion

  // region Connection Form Fields Tests

  @Test
  fun `form fields display name label`() {
    composeTestRule.setContent {
      ConnectionFormFields(
        name = "",
        onNameChange = {},
        address = "",
        onAddressChange = {},
        port = "",
        onPortChange = {},
        portError = null
      )
    }

    composeTestRule.onNodeWithText("Computer name").assertIsDisplayed()
  }

  @Test
  fun `form fields display host label`() {
    composeTestRule.setContent {
      ConnectionFormFields(
        name = "",
        onNameChange = {},
        address = "",
        onAddressChange = {},
        port = "",
        onPortChange = {},
        portError = null
      )
    }

    composeTestRule.onNodeWithText("Hostname / IP").assertIsDisplayed()
  }

  @Test
  fun `form fields display port label`() {
    composeTestRule.setContent {
      ConnectionFormFields(
        name = "",
        onNameChange = {},
        address = "",
        onAddressChange = {},
        port = "",
        onPortChange = {},
        portError = null
      )
    }

    composeTestRule.onNodeWithText("Port number").assertIsDisplayed()
  }

  @Test
  fun `form fields display port error when provided`() {
    composeTestRule.setContent {
      ConnectionFormFields(
        name = "",
        onNameChange = {},
        address = "",
        onAddressChange = {},
        port = "invalid",
        onPortChange = {},
        portError = "Port must be between 1 and 65535"
      )
    }

    composeTestRule.onNodeWithText("Port must be between 1 and 65535").assertIsDisplayed()
  }

  @Test
  fun `form fields display entered values`() {
    composeTestRule.setContent {
      ConnectionFormFields(
        name = "My Server",
        onNameChange = {},
        address = "192.168.1.50",
        onAddressChange = {},
        port = "3000",
        onPortChange = {},
        portError = null
      )
    }

    composeTestRule.onNodeWithText("My Server").assertIsDisplayed()
    composeTestRule.onNodeWithText("192.168.1.50").assertIsDisplayed()
    composeTestRule.onNodeWithText("3000").assertIsDisplayed()
  }

  @Test
  fun `form name change triggers onNameChange`() {
    var newName = ""

    composeTestRule.setContent {
      ConnectionFormFields(
        name = "",
        onNameChange = { newName = it },
        address = "",
        onAddressChange = {},
        port = "",
        onPortChange = {},
        portError = null
      )
    }

    composeTestRule.onNodeWithText("Computer name").performTextInput("Test")
    assertThat(newName).isEqualTo("Test")
  }

  @Test
  fun `form address change triggers onAddressChange`() {
    var newAddress = ""

    composeTestRule.setContent {
      ConnectionFormFields(
        name = "",
        onNameChange = {},
        address = "",
        onAddressChange = { newAddress = it },
        port = "",
        onPortChange = {},
        portError = null
      )
    }

    composeTestRule.onNodeWithText("Hostname / IP").performTextInput("192.168.1.1")
    assertThat(newAddress).isEqualTo("192.168.1.1")
  }

  @Test
  fun `form port change triggers onPortChange`() {
    var newPort = ""

    composeTestRule.setContent {
      ConnectionFormFields(
        name = "",
        onNameChange = {},
        address = "",
        onAddressChange = {},
        port = "",
        onPortChange = { newPort = it },
        portError = null
      )
    }

    composeTestRule.onNodeWithText("Port number").performTextInput("3000")
    assertThat(newPort).isEqualTo("3000")
  }

  // endregion
}
