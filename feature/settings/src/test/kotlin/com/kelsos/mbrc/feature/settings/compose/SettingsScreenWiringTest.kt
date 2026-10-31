package com.kelsos.mbrc.feature.settings.compose

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.settings.TrackAction
import com.kelsos.mbrc.core.common.utilities.AppInfo
import com.kelsos.mbrc.core.platform.service.ServiceRestarter
import com.kelsos.mbrc.core.ui.layout.ReadableContentDefaults
import com.kelsos.mbrc.feature.settings.R
import com.kelsos.mbrc.feature.settings.SettingsDialogType
import com.kelsos.mbrc.feature.settings.SettingsViewModel
import com.kelsos.mbrc.feature.settings.data.CallAction
import com.kelsos.mbrc.feature.settings.data.KeepScreenOn
import com.kelsos.mbrc.feature.settings.theme.Theme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.annotation.Config

/**
 * The shipped [SettingsScreen] renders [SettingsScreenContent] from [SettingsViewModel] state and
 * routes its actions back to the view model. The view model is a relaxed mock whose flows are
 * plain [MutableStateFlow]s a test can set.
 */
@RunWith(AndroidJUnit4::class)
class SettingsScreenWiringTest {
  @get:Rule
  val composeRule = createComposeRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val visibleDialog = MutableStateFlow<SettingsDialogType?>(null)
  private val viewModel = mockk<SettingsViewModel>(relaxed = true) {
    every { currentTheme } returns MutableStateFlow(Theme.System)
    every { keepScreenOn } returns MutableStateFlow(KeepScreenOn.Never)
    every { pluginUpdatesEnabled } returns MutableStateFlow(false)
    every { debugLoggingEnabled } returns MutableStateFlow(false)
    every { incomingCallAction } returns MutableStateFlow(CallAction.None)
    every { trackDefaultAction } returns MutableStateFlow(TrackAction.PlayNow)
    every { halfStarRatingEnabled } returns MutableStateFlow(true)
    every { showRatingOnPlayerEnabled } returns MutableStateFlow(false)
    every { visibleDialog } returns this@SettingsScreenWiringTest.visibleDialog
  }

  @Before
  fun startKoinForInjectedServices() {
    startKoin {
      modules(
        module {
          single<ServiceRestarter> { mockk(relaxed = true) }
          single<AppInfo> { mockk(relaxed = true) }
        }
      )
    }
  }

  @After
  fun stopKoinForInjectedServices() {
    stopKoin()
  }

  @Test
  fun `tapping the theme row opens the theme dialog through the view model`() {
    showSettings()

    composeRule.onNodeWithText(context.getString(R.string.setting_appearance_theme)).performClick()

    verify(exactly = 1) { viewModel.showDialog(SettingsDialogType.Theme) }
  }

  @Test
  fun `picking a theme in its dialog saves it and closes the dialog`() {
    visibleDialog.value = SettingsDialogType.Theme
    showSettings()

    composeRule.onNodeWithText(context.getString(R.string.theme_dark)).performClick()

    verify(exactly = 1) { viewModel.updateTheme(Theme.Dark) }
    verify(exactly = 1) { viewModel.hideDialog() }
  }

  @Test
  fun `toggling debug logging saves the new value`() {
    showSettings()

    composeRule.onNodeWithText(context.getString(R.string.setting_miscellaneous_debug_logging))
      .performScrollTo()
      .performClick()

    verify(exactly = 1) { viewModel.updateDebugLogging(true) }
  }

  @Test
  @Config(qualifiers = "w1280dp-h800dp")
  fun `on a wide window the settings rows stop at the readable width`() {
    showSettings()

    val row = composeRule
      .onNode(hasText(context.getString(R.string.setting_appearance_theme)) and hasClickAction())
      .fetchSemanticsNode()
    val rowWidth = with(composeRule.density) { row.boundsInRoot.width.toDp() }

    assertWithMessage("a 1280dp window should centre the rows at the readable width")
      .that(rowWidth.value)
      .isAtMost(ReadableContentDefaults.MaxWidth.value)
  }

  private fun showSettings() {
    composeRule.setContent {
      SettingsScreen(
        snackbarHostState = SnackbarHostState(),
        onOpenDrawer = {},
        viewModel = viewModel
      )
    }
  }
}
