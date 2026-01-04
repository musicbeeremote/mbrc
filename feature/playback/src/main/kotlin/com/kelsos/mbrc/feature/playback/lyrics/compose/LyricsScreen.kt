package com.kelsos.mbrc.feature.playback.lyrics.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.ui.compose.EmptyScreen
import com.kelsos.mbrc.core.ui.compose.ThinSlider
import com.kelsos.mbrc.core.ui.compose.WaveProgressIndicator
import com.kelsos.mbrc.feature.playback.R
import com.kelsos.mbrc.feature.playback.lyrics.LyricsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LyricsScreen(
  onCollapse: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: LyricsViewModel = koinViewModel()
) {
  val lyrics by viewModel.lyrics.collectAsState(initial = emptyList())
  val playingTrack by viewModel.playingTrack.collectAsState()
  val playingPosition by viewModel.playingPosition.collectAsState()
  val trackDetails by viewModel.trackDetails.collectAsState()
  val isPlaying by viewModel.isPlaying.collectAsState()

  LyricsScreenContent(
    lyrics = lyrics,
    playingTrack = playingTrack,
    playingPosition = playingPosition,
    composer = trackDetails.composer,
    isPlaying = isPlaying,
    onCollapse = onCollapse,
    onPlayPauseClick = viewModel::playPause,
    onSeek = { viewModel.seek(it.toInt()) },
    modifier = modifier
  )
}

@Composable
fun LyricsScreenContent(
  modifier: Modifier = Modifier,
  composer: String = "",
  lyrics: List<String>,
  playingTrack: TrackInfo,
  playingPosition: PlayingPosition,
  isPlaying: Boolean,
  onCollapse: () -> Unit,
  onPlayPauseClick: () -> Unit,
  onSeek: (Float) -> Unit
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.primary)
      .windowInsetsPadding(WindowInsets.statusBars)
  ) {
    // Header with collapse button and track info
    LyricsHeader(
      trackTitle = playingTrack.title,
      artistName = playingTrack.artist,
      composer = composer,
      onCollapse = onCollapse
    )

    // Lyrics content
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      if (lyrics.isEmpty()) {
        EmptyScreen(
          message = stringResource(R.string.no_lyrics),
          icon = Icons.Default.MusicNote,
          modifier = Modifier.fillMaxSize(),
          contentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(lyrics) { line ->
            LyricsLine(text = line)
          }
        }
      }
    }

    // Footer with progress and play/pause
    LyricsFooter(
      playingPosition = playingPosition,
      isPlaying = isPlaying,
      onPlayPauseClick = onPlayPauseClick,
      onSeek = onSeek
    )
  }
}

@Composable
private fun LyricsHeader(
  trackTitle: String,
  artistName: String,
  composer: String,
  onCollapse: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 8.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconButton(onClick = onCollapse) {
      Icon(
        imageVector = Icons.Default.KeyboardArrowDown,
        contentDescription = stringResource(R.string.lyrics_collapse),
        tint = MaterialTheme.colorScheme.onPrimary
      )
    }

    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = trackTitle.ifEmpty { stringResource(R.string.unknown_title) },
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onPrimary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
      )
      Text(
        text = artistName.ifEmpty { stringResource(R.string.unknown_artist) },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
      )
      if (composer.isNotBlank()) {
        Text(
          text = stringResource(R.string.track_details_composer) + ": " + composer,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          textAlign = TextAlign.Center
        )
      }
    }

    // Spacer to balance the collapse button
    Spacer(modifier = Modifier.size(48.dp))
  }
}

@Composable
private fun LyricsLine(text: String, modifier: Modifier = Modifier) {
  if (text.isBlank()) {
    // Spacer for empty lines (verse breaks)
    Spacer(modifier = modifier.height(16.dp))
  } else {
    Text(
      text = text,
      style = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.Bold,
        lineHeight = 32.sp
      ),
      color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.95f),
      modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    )
  }
}

@Composable
private fun LyricsFooter(
  playingPosition: PlayingPosition,
  isPlaying: Boolean,
  onPlayPauseClick: () -> Unit,
  onSeek: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  var seekPosition by remember { mutableFloatStateOf(0f) }
  var isSeeking by remember { mutableStateOf(false) }

  val progress = if (playingPosition.total > 0) {
    playingPosition.current.toFloat() / playingPosition.total.toFloat()
  } else {
    0f
  }

  val isStream = playingPosition.isStream

  Column(
    modifier = modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars)
      .padding(horizontal = 24.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    val sliderColor = MaterialTheme.colorScheme.onPrimary

    if (isStream) {
      // For streams, show wiggly wave indicator (not seekable)
      WaveProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        color = sliderColor,
        backgroundColor = sliderColor.copy(alpha = 0.3f)
      )
    } else {
      ThinSlider(
        value = if (isSeeking) seekPosition else progress,
        onValueChange = { newValue ->
          seekPosition = newValue
          isSeeking = true
        },
        onValueChangeFinished = {
          onSeek(seekPosition * playingPosition.total)
          isSeeking = false
        },
        modifier = Modifier.fillMaxWidth(),
        trackColor = sliderColor,
        thumbColor = sliderColor
      )
    }

    // Time display
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = playingPosition.currentMinutes,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
      )
      Text(
        text = playingPosition.totalMinutes,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Play/Pause button
    FilledIconButton(
      onClick = onPlayPauseClick,
      modifier = Modifier.size(64.dp),
      shape = CircleShape,
      colors = IconButtonDefaults.filledIconButtonColors(
        containerColor = MaterialTheme.colorScheme.onPrimary,
        contentColor = MaterialTheme.colorScheme.primary
      )
    ) {
      Icon(
        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
        contentDescription = stringResource(R.string.main_button_play_pause_description),
        modifier = Modifier.size(32.dp)
      )
    }
  }
}
