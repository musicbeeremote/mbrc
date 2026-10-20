package com.kelsos.mbrc.feature.playback.player.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.feature.playback.R

private val LiveDotSize = 6.dp
private val LiveDotSpacing = 4.dp

/**
 * The total duration of the playing track, or a red dot and "LIVE" for a stream, which has no
 * duration. The dot is smaller than the text line, so both forms are the same height and the
 * layout does not shift between a track and a stream.
 */
@Composable
internal fun TrackDurationLabel(
  position: PlayingPosition,
  style: TextStyle,
  color: Color,
  modifier: Modifier = Modifier
) {
  if (!position.isStream) {
    Text(text = position.totalMinutes, style = style, color = color, modifier = modifier)
    return
  }

  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(LiveDotSpacing)
  ) {
    Box(
      modifier = Modifier
        .size(LiveDotSize)
        .background(color = MaterialTheme.colorScheme.error, shape = CircleShape)
    )
    Text(text = stringResource(R.string.player_live), style = style, color = color)
  }
}
