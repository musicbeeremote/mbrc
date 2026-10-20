package com.kelsos.mbrc.feature.widgets.glance

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.testing.unit.GlanceAppWidgetUnitTest
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.testing.unit.hasContentDescriptionEqualTo
import androidx.glance.testing.unit.hasTextEqualTo
import androidx.test.core.app.ApplicationProvider
import com.kelsos.mbrc.feature.widgets.WidgetState
import kotlin.time.Duration.Companion.seconds
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private val TEST_TIMEOUT = 30.seconds

/**
 * The widget reads its fallback text and content descriptions from resources, so every test needs
 * a real context: Glance has none by default in a unit test.
 */
private fun widgetTest(block: GlanceAppWidgetUnitTest.() -> Unit) =
  runGlanceAppWidgetUnitTest(TEST_TIMEOUT) {
    setContext(ApplicationProvider.getApplicationContext())
    block()
  }

@RunWith(RobolectricTestRunner::class)
class WidgetContentTest {

  @Test
  fun normalWidget_displaysTrackInfo() = widgetTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(
        state = WidgetState(
          title = "Test Title",
          artist = "Test Artist",
          album = "Test Album",
          isPlaying = false
        ),
        actions = WidgetActions.noOp()
      )
    }

    onNode(hasTextEqualTo("Test Title")).assertExists()
    onNode(hasTextEqualTo("Test Artist")).assertExists()
    onNode(hasTextEqualTo("Test Album")).assertExists()
  }

  @Test
  fun normalWidget_displaysDefaultsWhenEmpty() = widgetTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(
        state = WidgetState(),
        actions = WidgetActions.noOp()
      )
    }

    onNode(hasTextEqualTo("Unknown title")).assertExists()
    onNode(hasTextEqualTo("Unknown artist")).assertExists()
    onNode(hasTextEqualTo("Unknown album")).assertExists()
  }

  @Test
  fun smallWidget_displaysTrackInfo() = widgetTest {
    setAppWidgetSize(DpSize(250.dp, 72.dp))

    provideComposable {
      SmallWidgetContent(
        state = WidgetState(
          title = "Test Title",
          artist = "Test Artist",
          isPlaying = true
        ),
        actions = WidgetActions.noOp()
      )
    }

    onNode(hasTextEqualTo("Test Title")).assertExists()
    onNode(hasTextEqualTo("Test Artist")).assertExists()
  }

  @Test
  fun smallWidget_displaysDefaultsWhenEmpty() = widgetTest {
    setAppWidgetSize(DpSize(250.dp, 72.dp))

    provideComposable {
      SmallWidgetContent(
        state = WidgetState(),
        actions = WidgetActions.noOp()
      )
    }

    onNode(hasTextEqualTo("Unknown title")).assertExists()
    onNode(hasTextEqualTo("Unknown artist")).assertExists()
  }

  @Test
  fun normalWidget_showsPlayButton_whenPaused() = widgetTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(
        state = WidgetState(isPlaying = false),
        actions = WidgetActions.noOp()
      )
    }

    onNode(hasContentDescriptionEqualTo("Play")).assertExists()
  }

  @Test
  fun normalWidget_showsPauseButton_whenPlaying() = widgetTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(
        state = WidgetState(isPlaying = true),
        actions = WidgetActions.noOp()
      )
    }

    onNode(hasContentDescriptionEqualTo("Pause")).assertExists()
  }

  @Test
  fun normalWidget_hasNavigationButtons() = widgetTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(
        state = WidgetState(),
        actions = WidgetActions.noOp()
      )
    }

    onNode(hasContentDescriptionEqualTo("Previous")).assertExists()
    onNode(hasContentDescriptionEqualTo("Next")).assertExists()
  }
}
