package com.kelsos.mbrc.core.common.state

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TrackInfoTest {

  // region BasicTrackInfo default values tests

  @Test
  fun `BasicTrackInfo should have empty strings as default`() {
    val info = BasicTrackInfo()
    assertThat(info.artist).isEmpty()
    assertThat(info.title).isEmpty()
    assertThat(info.album).isEmpty()
    assertThat(info.year).isEmpty()
    assertThat(info.path).isEmpty()
    assertThat(info.coverUrl).isEmpty()
  }

  @Test
  fun `BasicTrackInfo should have 0 as default duration`() {
    val info = BasicTrackInfo()
    assertThat(info.duration).isEqualTo(0)
  }

  // endregion

  // region BasicTrackInfo with values tests

  @Test
  fun `BasicTrackInfo should store all properties correctly`() {
    val info = BasicTrackInfo(
      artist = "Test Artist",
      title = "Test Title",
      album = "Test Album",
      year = "2023",
      path = "/music/test.mp3",
      coverUrl = "http://example.com/cover.jpg",
      duration = 180000L
    )

    assertThat(info.artist).isEqualTo("Test Artist")
    assertThat(info.title).isEqualTo("Test Title")
    assertThat(info.album).isEqualTo("Test Album")
    assertThat(info.year).isEqualTo("2023")
    assertThat(info.path).isEqualTo("/music/test.mp3")
    assertThat(info.coverUrl).isEqualTo("http://example.com/cover.jpg")
    assertThat(info.duration).isEqualTo(180000L)
  }

  // endregion

  // region orEmpty tests

  @Test
  fun `orEmpty should return same info when not null`() {
    val info: TrackInfo = BasicTrackInfo(title = "Test Song")
    val result = info.orEmpty()
    assertThat(result).isSameInstanceAs(info)
  }

  @Test
  fun `orEmpty should return empty BasicTrackInfo when null`() {
    val info: TrackInfo? = null
    val result = info.orEmpty()
    assertThat(result).isEqualTo(BasicTrackInfo())
  }

  // endregion

  // region toBasicTrackInfo tests

  @Test
  fun `toBasicTrackInfo should return same instance when already BasicTrackInfo`() {
    val info = BasicTrackInfo(title = "Test Song")
    val result = info.toBasicTrackInfo()
    assertThat(result).isSameInstanceAs(info)
  }

  @Test
  fun `toBasicTrackInfo should convert custom TrackInfo implementation`() {
    val customInfo = object : TrackInfo {
      override val artist: String = "Custom Artist"
      override val title: String = "Custom Title"
      override val album: String = "Custom Album"
      override val year: String = "2024"
      override val path: String = "/custom/path.mp3"
      override val coverUrl: String = "http://custom.com/cover.png"
      override val duration: Long = 240000L
    }

    val result = customInfo.toBasicTrackInfo()

    assertThat(result).isInstanceOf(BasicTrackInfo::class.java)
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
  fun `BasicTrackInfo should be equal when all properties match`() {
    val info1 = BasicTrackInfo(artist = "Artist", title = "Title")
    val info2 = BasicTrackInfo(artist = "Artist", title = "Title")
    assertThat(info1).isEqualTo(info2)
  }

  @Test
  fun `BasicTrackInfo should not be equal when properties differ`() {
    val info1 = BasicTrackInfo(artist = "Artist 1")
    val info2 = BasicTrackInfo(artist = "Artist 2")
    assertThat(info1).isNotEqualTo(info2)
  }

  @Test
  fun `BasicTrackInfo copy should work correctly`() {
    val original = BasicTrackInfo(artist = "Original", title = "Song")
    val copied = original.copy(artist = "Modified")

    assertThat(copied.artist).isEqualTo("Modified")
    assertThat(copied.title).isEqualTo("Song")
  }

  // endregion

  // region Edge case tests

  @Test
  fun `BasicTrackInfo should handle special characters`() {
    val info = BasicTrackInfo(
      title = "Test & <Song> \"Special\"",
      artist = "Artist's Name"
    )
    assertThat(info.title).isEqualTo("Test & <Song> \"Special\"")
    assertThat(info.artist).isEqualTo("Artist's Name")
  }

  @Test
  fun `BasicTrackInfo should handle unicode characters`() {
    val info = BasicTrackInfo(
      title = "日本語タイトル",
      artist = "Künstler",
      album = "专辑"
    )
    assertThat(info.title).isEqualTo("日本語タイトル")
    assertThat(info.artist).isEqualTo("Künstler")
    assertThat(info.album).isEqualTo("专辑")
  }

  @Test
  fun `BasicTrackInfo should handle negative duration`() {
    val info = BasicTrackInfo(duration = -1L)
    assertThat(info.duration).isEqualTo(-1L)
  }

  @Test
  fun `BasicTrackInfo should handle very long duration`() {
    val info = BasicTrackInfo(duration = Long.MAX_VALUE)
    assertThat(info.duration).isEqualTo(Long.MAX_VALUE)
  }

  // endregion
}
