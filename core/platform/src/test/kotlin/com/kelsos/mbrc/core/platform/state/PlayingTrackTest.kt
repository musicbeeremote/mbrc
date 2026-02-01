package com.kelsos.mbrc.core.platform.state

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayingTrackTest {

  // region Default values tests

  @Test
  fun `PlayingTrack should have empty strings as default`() {
    val track = PlayingTrack()
    assertThat(track.artist).isEmpty()
    assertThat(track.title).isEmpty()
    assertThat(track.album).isEmpty()
    assertThat(track.year).isEmpty()
    assertThat(track.path).isEmpty()
    assertThat(track.coverUrl).isEmpty()
  }

  @Test
  fun `PlayingTrack should have 0 as default duration`() {
    val track = PlayingTrack()
    assertThat(track.duration).isEqualTo(0)
  }

  // endregion

  // region orEmpty tests

  @Test
  fun `orEmpty should return same track when not null`() {
    val track = PlayingTrack(title = "Test Song")
    val result = track.orEmpty()
    assertThat(result).isSameInstanceAs(track)
  }

  @Test
  fun `orEmpty should return empty PlayingTrack when null`() {
    val track: PlayingTrack? = null
    val result = track.orEmpty()
    assertThat(result).isEqualTo(PlayingTrack())
  }

  // endregion

  // region toPlayingTrack tests

  @Test
  fun `toPlayingTrack should return same instance when already PlayingTrack`() {
    val track = PlayingTrack(title = "Test Song")
    val result = track.toPlayingTrack()
    assertThat(result).isSameInstanceAs(track)
  }

  @Test
  fun `toPlayingTrack should convert BasicTrackInfo to PlayingTrack`() {
    val basicInfo = BasicTrackInfo(
      artist = "Test Artist",
      title = "Test Title",
      album = "Test Album",
      year = "2023",
      path = "/music/test.mp3",
      coverUrl = "http://example.com/cover.jpg",
      duration = 180000L
    )
    val result = basicInfo.toPlayingTrack()

    assertThat(result).isInstanceOf(PlayingTrack::class.java)
    assertThat(result.artist).isEqualTo("Test Artist")
    assertThat(result.title).isEqualTo("Test Title")
    assertThat(result.album).isEqualTo("Test Album")
    assertThat(result.year).isEqualTo("2023")
    assertThat(result.path).isEqualTo("/music/test.mp3")
    assertThat(result.coverUrl).isEqualTo("http://example.com/cover.jpg")
    assertThat(result.duration).isEqualTo(180000L)
  }

  @Test
  fun `toPlayingTrack should preserve all fields when converting from TrackInfo`() {
    val customTrackInfo = object : com.kelsos.mbrc.core.common.state.TrackInfo {
      override val artist: String = "Custom Artist"
      override val title: String = "Custom Title"
      override val album: String = "Custom Album"
      override val year: String = "2024"
      override val path: String = "/custom/path.mp3"
      override val coverUrl: String = "http://custom.com/cover.png"
      override val duration: Long = 240000L
    }

    val result = customTrackInfo.toPlayingTrack()

    assertThat(result.artist).isEqualTo("Custom Artist")
    assertThat(result.title).isEqualTo("Custom Title")
    assertThat(result.album).isEqualTo("Custom Album")
    assertThat(result.year).isEqualTo("2024")
    assertThat(result.path).isEqualTo("/custom/path.mp3")
    assertThat(result.coverUrl).isEqualTo("http://custom.com/cover.png")
    assertThat(result.duration).isEqualTo(240000L)
  }

  // endregion

  // region Data class behavior tests

  @Test
  fun `PlayingTrack should be equal when all properties match`() {
    val track1 = PlayingTrack(artist = "Artist", title = "Title")
    val track2 = PlayingTrack(artist = "Artist", title = "Title")
    assertThat(track1).isEqualTo(track2)
  }

  @Test
  fun `PlayingTrack should not be equal when properties differ`() {
    val track1 = PlayingTrack(artist = "Artist 1")
    val track2 = PlayingTrack(artist = "Artist 2")
    assertThat(track1).isNotEqualTo(track2)
  }

  @Test
  fun `PlayingTrack copy should work correctly`() {
    val original = PlayingTrack(artist = "Original", title = "Song")
    val copied = original.copy(artist = "Modified")

    assertThat(copied.artist).isEqualTo("Modified")
    assertThat(copied.title).isEqualTo("Song")
  }

  // endregion
}
