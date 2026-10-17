package com.kelsos.mbrc.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.state.ConnectionStatus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawerBackPressTest {
  @get:Rule
  val composeRule = createAndroidComposeRule<ComponentActivity>()

  @Test
  fun `back closes an open drawer instead of leaving the app`() {
    lateinit var drawerState: DrawerState
    composeRule.setContent {
      drawerState = rememberDrawerState(DrawerValue.Open)
      ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
          DrawerContent(
            drawerState = drawerState,
            currentRoute = Screen.Home.route,
            connectionStatus = ConnectionStatus.Offline,
            connectionName = null,
            versionName = "1.7.1",
            onConnectionToggle = {},
            onNavigate = {}
          )
        }
      ) {
        Text("Now playing")
      }
    }

    composeRule.runOnIdle { composeRule.activity.onBackPressedDispatcher.onBackPressed() }
    composeRule.waitForIdle()

    assertWithMessage("back should be consumed by the open drawer (#388)")
      .that(drawerState.currentValue)
      .isEqualTo(DrawerValue.Closed)
    assertWithMessage("back fell through to the activity instead of closing the drawer")
      .that(composeRule.activity.isFinishing)
      .isFalse()
  }
}
