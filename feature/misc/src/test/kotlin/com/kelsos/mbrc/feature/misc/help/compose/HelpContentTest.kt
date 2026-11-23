package com.kelsos.mbrc.feature.misc.help.compose

import android.content.Context
import android.content.res.Resources
import android.util.AndroidRuntimeException
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.feature.misc.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HelpContentTest {
  @get:Rule
  val composeRule = createComposeRule()

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val opened = mutableListOf<String>()
  private val uriHandler = object : UriHandler {
    override fun openUri(uri: String) {
      opened.add(uri)
    }
  }

  @Test
  fun `a WebView that fails to load offers the help page in the browser`() {
    composeRule.setContent {
      CompositionLocalProvider(LocalUriHandler provides uriHandler) {
        HelpContent(versionName = "1.7.1", createWebView = {
          throw AndroidRuntimeException(
            Resources.NotFoundException("failed to redirect ResourcesImpl")
          )
        })
      }
    }

    composeRule.onNodeWithText(context.getString(R.string.help_webview_unavailable))
      .assertExists()
    composeRule.onNodeWithText(context.getString(R.string.help_open_in_browser)).performClick()

    assertThat(opened).containsExactly("https://mbrc.kelsos.net/help?version=1.7.1")
  }
}
