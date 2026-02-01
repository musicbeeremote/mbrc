package com.kelsos.mbrc.core.platform.media

import androidx.media3.common.MediaMetadata
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.platform.state.PlayingTrack
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaItemExtensionsTest {

  // region Basic property mapping tests

  @Test
  fun `toMediaItem should set title from PlayingTrack`() {
    val track = createPlayingTrack(title = "Test Song")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.title.toString()).isEqualTo("Test Song")
  }

  @Test
  fun `toMediaItem should set artist from PlayingTrack`() {
    val track = createPlayingTrack(artist = "Test Artist")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.artist.toString()).isEqualTo("Test Artist")
  }

  @Test
  fun `toMediaItem should set album title from PlayingTrack`() {
    val track = createPlayingTrack(album = "Test Album")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.albumTitle.toString()).isEqualTo("Test Album")
  }

  @Test
  fun `toMediaItem should set path as mediaId`() {
    val track = createPlayingTrack(path = "/music/song.mp3")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaId).isEqualTo("/music/song.mp3")
  }

  @Test
  fun `toMediaItem should set coverUrl as artworkUri`() {
    val track = createPlayingTrack(coverUrl = "http://example.com/cover.jpg")
    val mediaItem = track.toMediaItem()
    assertThat(
      mediaItem.mediaMetadata.artworkUri.toString()
    ).isEqualTo("http://example.com/cover.jpg")
  }

  // endregion

  // region Year parsing tests

  @Test
  fun `toMediaItem should parse valid year`() {
    val track = createPlayingTrack(year = "2023")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.releaseYear).isEqualTo(2023)
  }

  @Test
  fun `toMediaItem should set 0 for empty year`() {
    val track = createPlayingTrack(year = "")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.releaseYear).isEqualTo(0)
  }

  @Test
  fun `toMediaItem should set 0 for invalid year`() {
    val track = createPlayingTrack(year = "not a year")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.releaseYear).isEqualTo(0)
  }

  @Test
  fun `toMediaItem should set 0 for null-like year string`() {
    val track = createPlayingTrack(year = "null")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.releaseYear).isEqualTo(0)
  }

  // endregion

  // region Duration tests

  @Test
  fun `toMediaItem should set duration for valid value`() {
    val track = createPlayingTrack(duration = 180000L)
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.durationMs).isEqualTo(180000L)
  }

  @Test
  fun `toMediaItem should set duration for zero`() {
    val track = createPlayingTrack(duration = 0L)
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.durationMs).isEqualTo(0L)
  }

  @Test
  fun `toMediaItem should not set duration for negative value (stream)`() {
    val track = createPlayingTrack(duration = -1L)
    val mediaItem = track.toMediaItem()
    // When duration is negative (stream), it should not be set (null)
    assertThat(mediaItem.mediaMetadata.durationMs).isNull()
  }

  // endregion

  // region Display metadata tests

  @Test
  fun `toMediaItem should set displayTitle same as title`() {
    val track = createPlayingTrack(title = "Display Title")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.displayTitle.toString()).isEqualTo("Display Title")
  }

  @Test
  fun `toMediaItem should set subtitle to artist`() {
    val track = createPlayingTrack(artist = "Subtitle Artist")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.subtitle.toString()).isEqualTo("Subtitle Artist")
  }

  @Test
  fun `toMediaItem should set description to album`() {
    val track = createPlayingTrack(album = "Description Album")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.description.toString()).isEqualTo("Description Album")
  }

  @Test
  fun `toMediaItem should set mediaType to MUSIC`() {
    val track = createPlayingTrack()
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.mediaType).isEqualTo(MediaMetadata.MEDIA_TYPE_MUSIC)
  }

  // endregion

  // region Edge case tests

  @Test
  fun `toMediaItem should handle empty strings`() {
    val track = PlayingTrack()
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaId).isEmpty()
    assertThat(mediaItem.mediaMetadata.title.toString()).isEmpty()
    assertThat(mediaItem.mediaMetadata.artist.toString()).isEmpty()
    assertThat(mediaItem.mediaMetadata.albumTitle.toString()).isEmpty()
  }

  @Test
  fun `toMediaItem should handle special characters in title`() {
    val track = createPlayingTrack(title = "Test & <Song> \"Special\"")
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.title.toString()).isEqualTo("Test & <Song> \"Special\"")
  }

  @Test
  fun `toMediaItem should handle unicode characters`() {
    val track = createPlayingTrack(
      title = "日本語タイトル",
      artist = "Künstler",
      album = "专辑"
    )
    val mediaItem = track.toMediaItem()
    assertThat(mediaItem.mediaMetadata.title.toString()).isEqualTo("日本語タイトル")
    assertThat(mediaItem.mediaMetadata.artist.toString()).isEqualTo("Künstler")
    assertThat(mediaItem.mediaMetadata.albumTitle.toString()).isEqualTo("专辑")
  }

  // endregion

  private fun createPlayingTrack(
    artist: String = "Test Artist",
    title: String = "Test Title",
    album: String = "Test Album",
    year: String = "2023",
    path: String = "/music/test.mp3",
    coverUrl: String = "http://example.com/cover.jpg",
    duration: Long = 180000L
  ) = PlayingTrack(
    artist = artist,
    title = title,
    album = album,
    year = year,
    path = path,
    coverUrl = coverUrl,
    duration = duration
  )
}
