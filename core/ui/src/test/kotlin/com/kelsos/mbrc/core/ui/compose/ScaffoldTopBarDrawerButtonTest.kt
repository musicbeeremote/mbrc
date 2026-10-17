package com.kelsos.mbrc.core.ui.compose

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertWithMessage
import com.kelsos.mbrc.core.common.layout.WindowWidthClass
import com.kelsos.mbrc.core.ui.R
import com.kelsos.mbrc.core.ui.layout.LocalWindowWidthClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner

@RunWith(ParameterizedRobolectricTestRunner::class)
class ScaffoldTopBarDrawerButtonTest(private val widthClass: WindowWidthClass) {
  @get:Rule
  val composeRule = createComposeRule()

  private val drawerButtonDescription =
    ApplicationProvider.getApplicationContext<Context>().getString(R.string.drawer_open)

  @Test
  fun `the drawer button is shown and opens the drawer at every width`() {
    var drawerOpenRequests = 0
    composeRule.setContent {
      CompositionLocalProvider(LocalWindowWidthClass provides widthClass) {
        ScaffoldTopBar(
          state = TopBarState.Default,
          navigationIcon = NavigationIconType.Drawer,
          actionItems = emptyList(),
          menuItems = emptyList(),
          isTransparent = false,
          defaultTitle = "Now playing",
          onOpenDrawer = { drawerOpenRequests++ }
        )
      }
    }

    composeRule.onNodeWithContentDescription(drawerButtonDescription)
      .assertIsDisplayed()
      .performClick()

    assertWithMessage("the drawer is modal at $widthClass, so its button is the way in (#387)")
      .that(drawerOpenRequests)
      .isEqualTo(1)
  }

  companion object {
    @JvmStatic
    @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
    fun widthClasses(): List<Array<Any>> = WindowWidthClass.entries.map { arrayOf(it) }
  }
}
