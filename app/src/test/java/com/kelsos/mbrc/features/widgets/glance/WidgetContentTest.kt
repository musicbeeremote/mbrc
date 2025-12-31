package com.kelsos.mbrc.features.widgets.glance

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.testing.unit.runGlanceAppWidgetUnitTest
import androidx.glance.testing.unit.hasContentDescriptionEqualTo
import androidx.glance.testing.unit.hasTextEqualTo
import com.kelsos.mbrc.features.widgets.WidgetState
import org.junit.Test

class WidgetContentTest {

  @Test
  fun normalWidget_displaysTrackInfo() = runGlanceAppWidgetUnitTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(
        state = WidgetState(
          title = "Test Title",
          artist = "Test Artist",
          album = "Test Album",
          isPlaying = false
        )
      )
    }

    onNode(hasTextEqualTo("Test Title")).assertExists()
    onNode(hasTextEqualTo("Test Artist")).assertExists()
    onNode(hasTextEqualTo("Test Album")).assertExists()
  }

  @Test
  fun normalWidget_displaysDefaultsWhenEmpty() = runGlanceAppWidgetUnitTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(state = WidgetState())
    }

    onNode(hasTextEqualTo("Unknown title")).assertExists()
    onNode(hasTextEqualTo("Unknown artist")).assertExists()
    onNode(hasTextEqualTo("Unknown album")).assertExists()
  }

  @Test
  fun smallWidget_displaysTrackInfo() = runGlanceAppWidgetUnitTest {
    setAppWidgetSize(DpSize(250.dp, 72.dp))

    provideComposable {
      SmallWidgetContent(
        state = WidgetState(
          title = "Test Title",
          artist = "Test Artist",
          isPlaying = true
        )
      )
    }

    onNode(hasTextEqualTo("Test Title")).assertExists()
    onNode(hasTextEqualTo("Test Artist")).assertExists()
  }

  @Test
  fun smallWidget_displaysDefaultsWhenEmpty() = runGlanceAppWidgetUnitTest {
    setAppWidgetSize(DpSize(250.dp, 72.dp))

    provideComposable {
      SmallWidgetContent(state = WidgetState())
    }

    onNode(hasTextEqualTo("Unknown title")).assertExists()
    onNode(hasTextEqualTo("Unknown artist")).assertExists()
  }

  @Test
  fun normalWidget_showsPlayButton_whenPaused() = runGlanceAppWidgetUnitTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(
        state = WidgetState(isPlaying = false)
      )
    }

    onNode(hasContentDescriptionEqualTo("Play")).assertExists()
  }

  @Test
  fun normalWidget_showsPauseButton_whenPlaying() = runGlanceAppWidgetUnitTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(
        state = WidgetState(isPlaying = true)
      )
    }

    onNode(hasContentDescriptionEqualTo("Pause")).assertExists()
  }

  @Test
  fun normalWidget_hasNavigationButtons() = runGlanceAppWidgetUnitTest {
    setAppWidgetSize(DpSize(250.dp, 128.dp))

    provideComposable {
      NormalWidgetContent(state = WidgetState())
    }

    onNode(hasContentDescriptionEqualTo("Previous")).assertExists()
    onNode(hasContentDescriptionEqualTo("Next")).assertExists()
  }
}
