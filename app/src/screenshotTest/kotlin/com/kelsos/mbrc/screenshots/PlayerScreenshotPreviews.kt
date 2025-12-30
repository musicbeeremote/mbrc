package com.kelsos.mbrc.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.common.state.LfmRating
import com.kelsos.mbrc.common.state.PlayerState
import com.kelsos.mbrc.common.state.PlayingPosition
import com.kelsos.mbrc.common.state.PlayingTrack
import com.kelsos.mbrc.common.state.Repeat
import com.kelsos.mbrc.common.state.ShuffleMode
import com.kelsos.mbrc.common.state.TrackRating
import com.kelsos.mbrc.features.player.IPlayerActions
import com.kelsos.mbrc.features.player.PlaybackState
import com.kelsos.mbrc.features.player.VolumeState
import com.kelsos.mbrc.features.player.compose.PlayerScreenContent
import com.kelsos.mbrc.theme.RemoteTheme

/**
 * No-op implementation of IPlayerActions for preview purposes.
 */
private object PreviewPlayerActions : IPlayerActions {
  override val playPause: () -> Unit = {}
  override val previous: () -> Unit = {}
  override val next: () -> Unit = {}
  override val stop: () -> Unit = {}
  override val shuffle: () -> Unit = {}
  override val repeat: () -> Unit = {}
  override val mute: () -> Unit = {}
  override val changeVolume: (Int) -> Unit = {}
  override val seek: (Int) -> Unit = {}
  override val toggleFavorite: () -> Unit = {}
  override val toggleScrobbling: () -> Unit = {}
}

// =============================================================================
// Phone Previews - Portrait
// =============================================================================

@PreviewTest
@Preview(name = "Player Playing Light", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerPlayingLightPreview() {
  RemoteTheme(darkTheme = false) {
    PlayerScreenContent(
      playingTrack = samplePlayingTrack(),
      playingPosition = samplePlayingPosition(),
      trackRating = sampleNormalRating(),
      volumeState = VolumeState(volume = 75, mute = false),
      playbackState = PlaybackState(playerState = PlayerState.Playing),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Playing Dark", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerPlayingDarkPreview() {
  RemoteTheme(darkTheme = true) {
    PlayerScreenContent(
      playingTrack = samplePlayingTrack(),
      playingPosition = samplePlayingPosition(),
      trackRating = sampleNormalRating(),
      volumeState = VolumeState(volume = 75, mute = false),
      playbackState = PlaybackState(playerState = PlayerState.Playing),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Paused Light", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerPausedLightPreview() {
  RemoteTheme(darkTheme = false) {
    PlayerScreenContent(
      playingTrack = samplePausedTrack(),
      playingPosition = PlayingPosition(current = 180000, total = 354000),
      trackRating = sampleLovedRating(),
      volumeState = VolumeState(volume = 50, mute = false),
      playbackState = PlaybackState(playerState = PlayerState.Paused),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Paused Dark", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerPausedDarkPreview() {
  RemoteTheme(darkTheme = true) {
    PlayerScreenContent(
      playingTrack = samplePausedTrack(),
      playingPosition = PlayingPosition(current = 180000, total = 354000),
      trackRating = sampleLovedRating(),
      volumeState = VolumeState(volume = 50, mute = false),
      playbackState = PlaybackState(playerState = PlayerState.Paused),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

// =============================================================================
// Tablet Previews
// =============================================================================

@PreviewTest
@Preview(name = "Player Tablet Light", showBackground = true, device = Devices.TABLET)
@Composable
fun PlayerTabletLightPreview() {
  RemoteTheme(darkTheme = false) {
    PlayerScreenContent(
      playingTrack = samplePlayingTrack(),
      playingPosition = samplePlayingPosition(),
      trackRating = sampleNormalRating(),
      volumeState = VolumeState(volume = 75, mute = false),
      playbackState = PlaybackState(playerState = PlayerState.Playing),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Tablet Dark", showBackground = true, device = Devices.TABLET)
@Composable
fun PlayerTabletDarkPreview() {
  RemoteTheme(darkTheme = true) {
    PlayerScreenContent(
      playingTrack = samplePlayingTrack(),
      playingPosition = samplePlayingPosition(),
      trackRating = sampleNormalRating(),
      volumeState = VolumeState(volume = 75, mute = false),
      playbackState = PlaybackState(playerState = PlayerState.Playing),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

// =============================================================================
// Landscape Previews
// =============================================================================

@PreviewTest
@Preview(name = "Player Landscape Light", showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun PlayerLandscapeLightPreview() {
  RemoteTheme(darkTheme = false) {
    PlayerScreenContent(
      playingTrack = samplePlayingTrack(),
      playingPosition = samplePlayingPosition(),
      trackRating = sampleNormalRating(),
      volumeState = VolumeState(volume = 75, mute = false),
      playbackState = PlaybackState(playerState = PlayerState.Playing),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Landscape Dark", showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun PlayerLandscapeDarkPreview() {
  RemoteTheme(darkTheme = true) {
    PlayerScreenContent(
      playingTrack = samplePlayingTrack(),
      playingPosition = samplePlayingPosition(),
      trackRating = sampleNormalRating(),
      volumeState = VolumeState(volume = 75, mute = false),
      playbackState = PlaybackState(playerState = PlayerState.Playing),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

// =============================================================================
// Special States Previews
// =============================================================================

@PreviewTest
@Preview(name = "Player Muted Light", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerMutedLightPreview() {
  RemoteTheme(darkTheme = false) {
    PlayerScreenContent(
      playingTrack = sampleMutedTrack(),
      playingPosition = PlayingPosition(current = 240000, total = 482000),
      trackRating = sampleNormalRating(),
      volumeState = VolumeState(volume = 60, mute = true),
      playbackState = PlaybackState(playerState = PlayerState.Playing),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Muted Dark", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerMutedDarkPreview() {
  RemoteTheme(darkTheme = true) {
    PlayerScreenContent(
      playingTrack = sampleMutedTrack(),
      playingPosition = PlayingPosition(current = 240000, total = 482000),
      trackRating = sampleNormalRating(),
      volumeState = VolumeState(volume = 60, mute = true),
      playbackState = PlaybackState(playerState = PlayerState.Playing),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Shuffle Repeat Light", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerShuffleRepeatLightPreview() {
  RemoteTheme(darkTheme = false) {
    PlayerScreenContent(
      playingTrack = sampleShuffleTrack(),
      playingPosition = PlayingPosition(current = 60000, total = 431000),
      trackRating = sampleLovedRating(),
      volumeState = VolumeState(volume = 80, mute = false),
      playbackState = PlaybackState(
        playerState = PlayerState.Playing,
        shuffle = ShuffleMode.Shuffle,
        repeat = Repeat.One
      ),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Shuffle Repeat Dark", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerShuffleRepeatDarkPreview() {
  RemoteTheme(darkTheme = true) {
    PlayerScreenContent(
      playingTrack = sampleShuffleTrack(),
      playingPosition = PlayingPosition(current = 60000, total = 431000),
      trackRating = sampleLovedRating(),
      volumeState = VolumeState(volume = 80, mute = false),
      playbackState = PlaybackState(
        playerState = PlayerState.Playing,
        shuffle = ShuffleMode.Shuffle,
        repeat = Repeat.One
      ),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Empty Track Light", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerEmptyTrackLightPreview() {
  RemoteTheme(darkTheme = false) {
    PlayerScreenContent(
      playingTrack = PlayingTrack(),
      playingPosition = PlayingPosition(),
      trackRating = TrackRating(),
      volumeState = VolumeState(),
      playbackState = PlaybackState(),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

@PreviewTest
@Preview(name = "Player Empty Track Dark", showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun PlayerEmptyTrackDarkPreview() {
  RemoteTheme(darkTheme = true) {
    PlayerScreenContent(
      playingTrack = PlayingTrack(),
      playingPosition = PlayingPosition(),
      trackRating = TrackRating(),
      volumeState = VolumeState(),
      playbackState = PlaybackState(),
      actions = PreviewPlayerActions,
      hasLyrics = false,
      onTrackInfoClick = {},
      onLyricsClick = {},
      onOutputClick = {}
    )
  }
}

// =============================================================================
// Sample Data Helpers
// =============================================================================

private fun samplePlayingTrack() = PlayingTrack(
  artist = "Pink Floyd",
  title = "Time",
  album = "The Dark Side of the Moon",
  year = "1973",
  path = "/music/time.mp3",
  coverUrl = "",
  duration = 413000
)

private fun samplePlayingPosition() = PlayingPosition(
  current = 125000,
  total = 413000
)

private fun samplePausedTrack() = PlayingTrack(
  artist = "Queen",
  title = "Bohemian Rhapsody",
  album = "A Night at the Opera",
  year = "1975",
  path = "/music/bohemian.mp3",
  coverUrl = "",
  duration = 354000
)

private fun sampleMutedTrack() = PlayingTrack(
  artist = "Led Zeppelin",
  title = "Stairway to Heaven",
  album = "Led Zeppelin IV",
  year = "1971",
  path = "/music/stairway.mp3",
  coverUrl = "",
  duration = 482000
)

private fun sampleShuffleTrack() = PlayingTrack(
  artist = "The Beatles",
  title = "Hey Jude",
  album = "Hey Jude",
  year = "1968",
  path = "/music/heyjude.mp3",
  coverUrl = "",
  duration = 431000
)

private fun sampleNormalRating() = TrackRating(
  lfmRating = LfmRating.Normal,
  rating = 4f
)

private fun sampleLovedRating() = TrackRating(
  lfmRating = LfmRating.Loved,
  rating = 5f
)
