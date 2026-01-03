package com.kelsos.mbrc.feature.widgets.glance

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.kelsos.mbrc.core.ui.R as CoreUiR
import com.kelsos.mbrc.feature.widgets.R
import com.kelsos.mbrc.feature.widgets.WidgetState

/**
 * A no-op action for testing that doesn't involve coroutines.
 * Using a simple object instead of `action {}` composable to avoid UncompletedCoroutinesError in tests.
 */
private object NoOpAction : Action

data class WidgetActions(
  val onPrevious: Action,
  val onPlayPause: Action,
  val onNext: Action,
  val onOpenApp: Action
) {
  companion object {
    val Default = WidgetActions(
      onPrevious = actionRunCallback<PreviousTrackAction>(),
      onPlayPause = actionRunCallback<PlayPauseAction>(),
      onNext = actionRunCallback<NextTrackAction>(),
      onOpenApp = actionRunCallback<OpenAppAction>()
    )

    /**
     * Creates no-op actions for testing. Uses a simple object that doesn't involve coroutines.
     */
    fun noOp() = WidgetActions(
      onPrevious = NoOpAction,
      onPlayPause = NoOpAction,
      onNext = NoOpAction,
      onOpenApp = NoOpAction
    )
  }
}

private val titleStyle: TextStyle
  @Composable get() = TextStyle(
    color = GlanceTheme.colors.onSurface,
    fontSize = 14.sp,
    fontWeight = FontWeight.Bold
  )

private val subtitleStyle: TextStyle
  @Composable get() = TextStyle(
    color = GlanceTheme.colors.onSurfaceVariant,
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal
  )

private const val CONTROL_BUTTON_SIZE = 40
private const val NORMAL_WIDGET_HEIGHT = 128
private const val SMALL_WIDGET_HEIGHT = 72

/**
 * Normal widget content (128dp height).
 * Shows: Album cover, title, artist, album, and playback controls.
 */
@Composable
fun NormalWidgetContent(state: WidgetState, actions: WidgetActions = WidgetActions.Default) {
  Row(
    modifier = GlanceModifier
      .fillMaxWidth()
      .height(NORMAL_WIDGET_HEIGHT.dp)
      .background(GlanceTheme.colors.background)
      .clickable(actions.onOpenApp),
    verticalAlignment = Alignment.Top
  ) {
    // Album cover
    AlbumCover(
      bitmap = state.coverBitmap,
      size = NORMAL_WIDGET_HEIGHT.dp,
      modifier = GlanceModifier.clickable(actions.onOpenApp)
    )

    // Track info and controls
    Column(
      modifier = GlanceModifier
        .fillMaxHeight()
        .defaultWeight()
    ) {
      // Track info section
      Column(
        modifier = GlanceModifier
          .fillMaxWidth()
          .defaultWeight()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Title
        Text(
          text = state.title.ifEmpty { "Unknown title" },
          style = titleStyle,
          maxLines = 1
        )
        Spacer(modifier = GlanceModifier.height(2.dp))
        // Artist
        Text(
          text = state.artist.ifEmpty { "Unknown artist" },
          style = subtitleStyle,
          maxLines = 1
        )
        Spacer(modifier = GlanceModifier.height(2.dp))
        // Album
        Text(
          text = state.album.ifEmpty { "Unknown album" },
          style = subtitleStyle,
          maxLines = 1
        )
      }

      // Playback controls
      PlaybackControls(
        isPlaying = state.isPlaying,
        actions = actions,
        modifier = GlanceModifier
          .fillMaxWidth()
          .height(CONTROL_BUTTON_SIZE.dp)
      )
    }
  }
}

/**
 * Small widget content (72dp height).
 * Shows: Album cover, title - artist, and playback controls.
 */
@Composable
fun SmallWidgetContent(state: WidgetState, actions: WidgetActions = WidgetActions.Default) {
  Row(
    modifier = GlanceModifier
      .fillMaxWidth()
      .height(SMALL_WIDGET_HEIGHT.dp)
      .background(GlanceTheme.colors.background)
      .clickable(actions.onOpenApp),
    verticalAlignment = Alignment.Top
  ) {
    // Album cover
    AlbumCover(
      bitmap = state.coverBitmap,
      size = SMALL_WIDGET_HEIGHT.dp,
      modifier = GlanceModifier.clickable(actions.onOpenApp)
    )

    // Track info and controls
    Column(
      modifier = GlanceModifier
        .fillMaxHeight()
        .defaultWeight()
    ) {
      // Track info section (single row: title - artist)
      Row(
        modifier = GlanceModifier
          .fillMaxWidth()
          .defaultWeight()
          .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = state.title.ifEmpty { "Unknown title" },
          style = titleStyle,
          maxLines = 1
        )
        Text(
          text = " - ",
          style = subtitleStyle
        )
        Text(
          text = state.artist.ifEmpty { "Unknown artist" },
          style = subtitleStyle,
          maxLines = 1
        )
      }

      // Playback controls
      PlaybackControls(
        isPlaying = state.isPlaying,
        actions = actions,
        modifier = GlanceModifier
          .fillMaxWidth()
          .height(CONTROL_BUTTON_SIZE.dp)
      )
    }
  }
}

/**
 * Album cover image with placeholder fallback.
 */
@Composable
private fun AlbumCover(
  bitmap: Bitmap?,
  size: androidx.compose.ui.unit.Dp,
  modifier: GlanceModifier = GlanceModifier
) {
  val imageProvider = if (bitmap != null) {
    ImageProvider(bitmap)
  } else {
    ImageProvider(CoreUiR.drawable.ic_image_no_cover)
  }

  Image(
    provider = imageProvider,
    contentDescription = "Album cover",
    modifier = modifier
      .size(size)
      .cornerRadius(4.dp)
  )
}

/**
 * Playback control buttons (previous, play/pause, next).
 */
@Composable
private fun PlaybackControls(
  isPlaying: Boolean,
  actions: WidgetActions,
  modifier: GlanceModifier = GlanceModifier
) {
  Row(
    modifier = modifier.background(GlanceTheme.colors.surface),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Previous button
    ControlButton(
      iconRes = R.drawable.baseline_skip_previous_24,
      contentDescription = "Previous",
      onClick = actions.onPrevious,
      modifier = GlanceModifier.defaultWeight()
    )

    // Play/Pause button
    ControlButton(
      iconRes = if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24,
      contentDescription = if (isPlaying) "Pause" else "Play",
      onClick = actions.onPlayPause,
      modifier = GlanceModifier.defaultWeight()
    )

    // Next button
    ControlButton(
      iconRes = R.drawable.baseline_skip_next_24,
      contentDescription = "Next",
      onClick = actions.onNext,
      modifier = GlanceModifier.defaultWeight()
    )
  }
}

@Composable
private fun ControlButton(
  iconRes: Int,
  contentDescription: String,
  onClick: Action,
  modifier: GlanceModifier = GlanceModifier
) {
  Image(
    provider = ImageProvider(iconRes),
    contentDescription = contentDescription,
    modifier = modifier
      .fillMaxHeight()
      .clickable(onClick)
      .padding(horizontal = 12.dp, vertical = 8.dp)
  )
}
