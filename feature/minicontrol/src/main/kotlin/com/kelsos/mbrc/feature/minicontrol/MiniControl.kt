package com.kelsos.mbrc.feature.minicontrol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.ui.R as CoreUiR
import com.kelsos.mbrc.core.ui.compose.WaveProgressIndicator
import org.koin.androidx.compose.koinViewModel

@Composable
fun MiniControl(
  onNavigateToPlayer: () -> Unit,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  viewModel: MiniControlViewModel = koinViewModel()
) {
  val state by viewModel.state.collectAsStateWithLifecycle(initialValue = MiniControlState())

  LaunchedEffect(Unit) {
    viewModel.events.collect { event ->
      val message = when (event) {
        MiniControlUiMessages.NetworkUnavailable -> "Network unavailable"
        MiniControlUiMessages.ActionFailed -> "Action failed"
      }
      snackbarHostState.showSnackbar(message)
    }
  }

  MiniControlContent(
    state = state,
    onNavigateToPlayer = onNavigateToPlayer,
    onPreviousClick = { viewModel.perform(MiniControlAction.PlayPrevious) },
    onPlayPauseClick = { viewModel.perform(MiniControlAction.PlayPause) },
    onNextClick = { viewModel.perform(MiniControlAction.PlayNext) },
    modifier = modifier
  )
}

@Composable
fun MiniControlContent(
  state: MiniControlState,
  onNavigateToPlayer: () -> Unit,
  onPreviousClick: () -> Unit,
  onPlayPauseClick: () -> Unit,
  onNextClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isStream = state.playingPosition.isStream
  val progress = if (!isStream && state.playingPosition.total > 0) {
    state.playingPosition.current.toFloat() / state.playingPosition.total.toFloat()
  } else {
    0f
  }

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
    color = MaterialTheme.colorScheme.surfaceVariant
  ) {
    Column {
      if (isStream) {
        // Wiggly wave progress for streams
        WaveProgressIndicator(
          modifier = Modifier.fillMaxWidth(),
          color = MaterialTheme.colorScheme.primary,
          backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
          height = 6.dp,
          waveAmplitude = 1.dp,
          strokeWidth = 2.dp
        )
      } else {
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp),
          trackColor = MaterialTheme.colorScheme.surfaceVariant,
          gapSize = 0.dp,
          drawStopIndicator = {}
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onNavigateToPlayer)
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AsyncImage(
          model = state.playingTrack.coverUrl.ifEmpty { null },
          contentDescription = stringResource(R.string.description_album_cover),
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(4.dp)),
          contentScale = ContentScale.Crop,
          placeholder = painterResource(CoreUiR.drawable.ic_image_no_cover),
          error = painterResource(CoreUiR.drawable.ic_image_no_cover)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = state.playingTrack.title.ifEmpty {
              stringResource(R.string.unknown_title)
            },
            style = MaterialTheme.typography.bodyMedium.copy(
              lineHeight = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = state.playingTrack.artist.ifEmpty {
              stringResource(R.string.unknown_artist)
            },
            style = MaterialTheme.typography.bodySmall.copy(
              lineHeight = 16.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        IconButton(onClick = onPreviousClick) {
          Icon(
            imageVector = Icons.Default.SkipPrevious,
            contentDescription = stringResource(R.string.main_button_previous_description),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        IconButton(onClick = onPlayPauseClick) {
          Icon(
            imageVector = if (state.playingState == PlayerState.Playing) {
              Icons.Default.Pause
            } else {
              Icons.Default.PlayArrow
            },
            contentDescription = stringResource(R.string.main_button_play_pause_description),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(32.dp)
          )
        }

        IconButton(onClick = onNextClick) {
          Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = stringResource(R.string.main_button_next_description),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
