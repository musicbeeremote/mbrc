package com.kelsos.mbrc.screenshots

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.common.state.PlayingPosition
import com.kelsos.mbrc.common.state.PlayingTrack
import com.kelsos.mbrc.features.lyrics.compose.LyricsScreenContent
import com.kelsos.mbrc.theme.RemoteTheme

private val sampleLyrics = listOf(
  "Verse 1",
  "This is the first line of the song",
  "Here comes the second line with more words",
  "And now we have the third line",
  "",
  "Chorus",
  "Singing along to the melody",
  "The rhythm keeps on playing",
  "Music fills the air tonight",
  "",
  "Verse 2",
  "Another verse begins right here",
  "The story continues to unfold",
  "Each word carries meaning"
)

private val sampleTrack = PlayingTrack(
  title = "Moonlight Sonata",
  artist = "Ludwig van Beethoven",
  album = "Classical Favorites"
)

private val samplePosition = PlayingPosition(
  current = 145000L, // 2:25
  total = 269000L // 4:29
)

@PreviewTest
@Preview(name = "Lyrics With Content Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LyricsWithContentPreviewLight() {
  RemoteTheme(darkTheme = false) {
    LyricsScreenContent(
      lyrics = sampleLyrics,
      playingTrack = sampleTrack,
      playingPosition = samplePosition,
      isPlaying = true,
      onCollapse = {},
      onPlayPauseClick = {},
      onSeek = {},
      modifier = Modifier.fillMaxSize()
    )
  }
}

@PreviewTest
@Preview(name = "Lyrics With Content Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LyricsWithContentPreviewDark() {
  RemoteTheme(darkTheme = true) {
    LyricsScreenContent(
      lyrics = sampleLyrics,
      playingTrack = sampleTrack,
      playingPosition = samplePosition,
      isPlaying = true,
      onCollapse = {},
      onPlayPauseClick = {},
      onSeek = {},
      modifier = Modifier.fillMaxSize()
    )
  }
}

@PreviewTest
@Preview(name = "Lyrics Empty Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LyricsEmptyPreviewLight() {
  RemoteTheme(darkTheme = false) {
    LyricsScreenContent(
      lyrics = emptyList(),
      playingTrack = sampleTrack,
      playingPosition = samplePosition,
      isPlaying = false,
      onCollapse = {},
      onPlayPauseClick = {},
      onSeek = {},
      modifier = Modifier.fillMaxSize()
    )
  }
}

@PreviewTest
@Preview(name = "Lyrics Empty Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LyricsEmptyPreviewDark() {
  RemoteTheme(darkTheme = true) {
    LyricsScreenContent(
      lyrics = emptyList(),
      playingTrack = sampleTrack,
      playingPosition = samplePosition,
      isPlaying = false,
      onCollapse = {},
      onPlayPauseClick = {},
      onSeek = {},
      modifier = Modifier.fillMaxSize()
    )
  }
}

@PreviewTest
@Preview(name = "Lyrics Paused Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LyricsPausedPreviewLight() {
  RemoteTheme(darkTheme = false) {
    LyricsScreenContent(
      lyrics = sampleLyrics,
      playingTrack = sampleTrack,
      playingPosition = samplePosition,
      isPlaying = false,
      onCollapse = {},
      onPlayPauseClick = {},
      onSeek = {},
      modifier = Modifier.fillMaxSize()
    )
  }
}

@PreviewTest
@Preview(name = "Lyrics Paused Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LyricsPausedPreviewDark() {
  RemoteTheme(darkTheme = true) {
    LyricsScreenContent(
      lyrics = sampleLyrics,
      playingTrack = sampleTrack,
      playingPosition = samplePosition,
      isPlaying = false,
      onCollapse = {},
      onPlayPauseClick = {},
      onSeek = {},
      modifier = Modifier.fillMaxSize()
    )
  }
}
