package com.kelsos.mbrc.ui

import android.content.Context
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class LocalNetworkDeniedNoticeTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val message = context.getString(R.string.local_network_denied_banner)
  private val grant = context.getString(R.string.local_network_denied_grant)

  private var denials by mutableIntStateOf(0)
  private var grantRequests = 0

  private fun showNotice() {
    composeTestRule.setContent {
      val snackbarHostState = remember { SnackbarHostState() }
      LocalNetworkDeniedNotice(
        denied = true,
        denials = denials,
        snackbarHostState = snackbarHostState,
        onGrant = { grantRequests++ }
      )
      SnackbarHost(hostState = snackbarHostState)
    }
  }

  @Test
  fun `a refused prompt after grant brings the notice back`() {
    denials = 1
    showNotice()
    composeTestRule.onNodeWithText(message).assertExists()

    composeTestRule.onNodeWithText(grant).performClick()
    composeTestRule.waitForIdle()
    assertWithMessage("Grant should hand over to the permission request")
      .that(grantRequests)
      .isEqualTo(1)
    composeTestRule.onNodeWithText(message).assertDoesNotExist()

    denials = 2
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText(message).assertExists()
  }
}
