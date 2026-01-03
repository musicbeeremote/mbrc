package com.kelsos.mbrc.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.PlayerState
import com.kelsos.mbrc.core.common.state.PlayingPosition
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import com.kelsos.mbrc.feature.minicontrol.MiniControlContent
import com.kelsos.mbrc.feature.minicontrol.MiniControlState

@PreviewTest
@Preview(name = "MiniControl Playing Light", showBackground = true, widthDp = 360)
@Composable
fun MiniControlPlayingLightPreview() {
  RemoteTheme(darkTheme = false) {
    MiniControlContent(
      state = MiniControlState(
        playingTrack = BasicTrackInfo(
          artist = "Parov Stelar",
          title = "All Night",
          album = "The Princess"
        ),
        playingPosition = PlayingPosition(current = 45000, total = 180000),
        playingState = PlayerState.Playing
      ),
      onNavigateToPlayer = {},
      onPreviousClick = {},
      onPlayPauseClick = {},
      onNextClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "MiniControl Playing Dark", showBackground = true, widthDp = 360)
@Composable
fun MiniControlPlayingDarkPreview() {
  RemoteTheme(darkTheme = true) {
    MiniControlContent(
      state = MiniControlState(
        playingTrack = BasicTrackInfo(
          artist = "Parov Stelar",
          title = "All Night",
          album = "The Princess"
        ),
        playingPosition = PlayingPosition(current = 45000, total = 180000),
        playingState = PlayerState.Playing
      ),
      onNavigateToPlayer = {},
      onPreviousClick = {},
      onPlayPauseClick = {},
      onNextClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "MiniControl Paused Light", showBackground = true, widthDp = 360)
@Composable
fun MiniControlPausedLightPreview() {
  RemoteTheme(darkTheme = false) {
    MiniControlContent(
      state = MiniControlState(
        playingTrack = BasicTrackInfo(
          artist = "Caravan Palace",
          title = "Lone Digger",
          album = "Robot Face"
        ),
        playingPosition = PlayingPosition(current = 120000, total = 240000),
        playingState = PlayerState.Paused
      ),
      onNavigateToPlayer = {},
      onPreviousClick = {},
      onPlayPauseClick = {},
      onNextClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "MiniControl Paused Dark", showBackground = true, widthDp = 360)
@Composable
fun MiniControlPausedDarkPreview() {
  RemoteTheme(darkTheme = true) {
    MiniControlContent(
      state = MiniControlState(
        playingTrack = BasicTrackInfo(
          artist = "Caravan Palace",
          title = "Lone Digger",
          album = "Robot Face"
        ),
        playingPosition = PlayingPosition(current = 120000, total = 240000),
        playingState = PlayerState.Paused
      ),
      onNavigateToPlayer = {},
      onPreviousClick = {},
      onPlayPauseClick = {},
      onNextClick = {}
    )
  }
}
