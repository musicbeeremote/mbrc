package com.kelsos.mbrc.feature.widgets.glance

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.testing.unit.hasContentDescriptionEqualTo
import androidx.glance.testing.unit.hasTextEqualTo
import com.kelsos.mbrc.feature.widgets.WidgetState
import kotlin.time.Duration.Companion.seconds
import org.junit.Test

private val TEST_TIMEOUT = 30.seconds

class WidgetContentTest {

  @Test
  fun normalWidget_displaysTrackInfo() = runGlanceAppWidgetUnitTest(TEST_TIMEOUT) {
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
  fun normalWidget_displaysDefaultsWhenEmpty() = runGlanceAppWidgetUnitTest(TEST_TIMEOUT) {
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
  fun smallWidget_displaysTrackInfo() = runGlanceAppWidgetUnitTest(TEST_TIMEOUT) {
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
  fun smallWidget_displaysDefaultsWhenEmpty() = runGlanceAppWidgetUnitTest(TEST_TIMEOUT) {
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
  fun normalWidget_showsPlayButton_whenPaused() = runGlanceAppWidgetUnitTest(TEST_TIMEOUT) {
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
  fun normalWidget_showsPauseButton_whenPlaying() = runGlanceAppWidgetUnitTest(TEST_TIMEOUT) {
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
  fun normalWidget_hasNavigationButtons() = runGlanceAppWidgetUnitTest(TEST_TIMEOUT) {
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
