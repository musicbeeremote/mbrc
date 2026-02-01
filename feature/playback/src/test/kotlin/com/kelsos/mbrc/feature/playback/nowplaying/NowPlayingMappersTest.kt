package com.kelsos.mbrc.feature.playback.nowplaying

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.data.nowplaying.NowPlayingEntity
import com.kelsos.mbrc.core.networking.dto.NowPlayingDto
import org.junit.Test

class NowPlayingMappersTest {

  // region NowPlayingDto to NowPlayingEntity mapping

  @Test
  fun `toEntity should map title from dto`() {
    val dto =
      NowPlayingDto(title = "Bohemian Rhapsody", artist = "Queen", path = "path", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.title).isEqualTo("Bohemian Rhapsody")
  }

  @Test
  fun `toEntity should map artist from dto`() {
    val dto = NowPlayingDto(title = "Song", artist = "Pink Floyd", path = "path", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("Pink Floyd")
  }

  @Test
  fun `toEntity should map path from dto`() {
    val dto =
      NowPlayingDto(title = "Song", artist = "Artist", path = "C:\\Music\\song.mp3", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.path).isEqualTo("C:\\Music\\song.mp3")
  }

  @Test
  fun `toEntity should map position from dto`() {
    val dto = NowPlayingDto(title = "Song", artist = "Artist", path = "path", position = 5)
    val entity = dto.toEntity()
    assertThat(entity.position).isEqualTo(5)
  }

  @Test
  fun `toEntity should handle empty title`() {
    val dto = NowPlayingDto(title = "", artist = "Artist", path = "path", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.title).isEmpty()
  }

  @Test
  fun `toEntity should handle empty artist`() {
    val dto = NowPlayingDto(title = "Song", artist = "", path = "path", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.artist).isEmpty()
  }

  @Test
  fun `toEntity should handle empty path`() {
    val dto = NowPlayingDto(title = "Song", artist = "Artist", path = "", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.path).isEmpty()
  }

  @Test
  fun `toEntity should handle position zero`() {
    val dto = NowPlayingDto(title = "Song", artist = "Artist", path = "path", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.position).isEqualTo(0)
  }

  @Test
  fun `toEntity should handle large position values`() {
    val dto = NowPlayingDto(title = "Song", artist = "Artist", path = "path", position = 10000)
    val entity = dto.toEntity()
    assertThat(entity.position).isEqualTo(10000)
  }

  @Test
  fun `toEntity should handle special characters in title`() {
    val dto = NowPlayingDto(
      title = "It's a Long Way to the Top (If You Wanna Rock 'n' Roll)",
      artist = "AC/DC",
      path = "path",
      position = 0
    )
    val entity = dto.toEntity()
    assertThat(entity.title).isEqualTo("It's a Long Way to the Top (If You Wanna Rock 'n' Roll)")
  }

  @Test
  fun `toEntity should handle unicode characters`() {
    val dto = NowPlayingDto(title = "Glósóli", artist = "Sigur Rós", path = "path", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.title).isEqualTo("Glósóli")
    assertThat(entity.artist).isEqualTo("Sigur Rós")
  }

  @Test
  fun `toEntity should set default id to 0`() {
    val dto = NowPlayingDto(title = "Song", artist = "Artist", path = "path", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.id).isEqualTo(0)
  }

  @Test
  fun `toEntity should set default dateAdded to 0`() {
    val dto = NowPlayingDto(title = "Song", artist = "Artist", path = "path", position = 0)
    val entity = dto.toEntity()
    assertThat(entity.dateAdded).isEqualTo(0)
  }

  @Test
  fun `toEntity should handle default dto values`() {
    val dto = NowPlayingDto()
    val entity = dto.toEntity()
    assertThat(entity.title).isEmpty()
    assertThat(entity.artist).isEmpty()
    assertThat(entity.path).isEmpty()
    assertThat(entity.position).isEqualTo(0)
  }

  // endregion

  // region NowPlayingEntity to NowPlaying mapping

  @Test
  fun `toNowPlaying should map id from entity`() {
    val entity = createNowPlayingEntity(id = 42)
    val nowPlaying = entity.toNowPlaying()
    assertThat(nowPlaying.id).isEqualTo(42)
  }

  @Test
  fun `toNowPlaying should map title from entity`() {
    val entity = createNowPlayingEntity(title = "Wish You Were Here")
    val nowPlaying = entity.toNowPlaying()
    assertThat(nowPlaying.title).isEqualTo("Wish You Were Here")
  }

  @Test
  fun `toNowPlaying should map artist from entity`() {
    val entity = createNowPlayingEntity(artist = "The Beatles")
    val nowPlaying = entity.toNowPlaying()
    assertThat(nowPlaying.artist).isEqualTo("The Beatles")
  }

  @Test
  fun `toNowPlaying should map path from entity`() {
    val entity = createNowPlayingEntity(path = "D:\\Music\\track.flac")
    val nowPlaying = entity.toNowPlaying()
    assertThat(nowPlaying.path).isEqualTo("D:\\Music\\track.flac")
  }

  @Test
  fun `toNowPlaying should map position from entity`() {
    val entity = createNowPlayingEntity(position = 7)
    val nowPlaying = entity.toNowPlaying()
    assertThat(nowPlaying.position).isEqualTo(7)
  }

  @Test
  fun `toNowPlaying should handle empty strings`() {
    val entity = NowPlayingEntity(title = "", artist = "", path = "", position = 0)
    val nowPlaying = entity.toNowPlaying()
    assertThat(nowPlaying.title).isEmpty()
    assertThat(nowPlaying.artist).isEmpty()
    assertThat(nowPlaying.path).isEmpty()
  }

  @Test
  fun `toNowPlaying should handle entity with default id`() {
    val entity = NowPlayingEntity(title = "Song", artist = "Artist", path = "path", position = 0)
    val nowPlaying = entity.toNowPlaying()
    assertThat(nowPlaying.id).isEqualTo(0)
  }

  @Test
  fun `toNowPlaying should not include dateAdded in domain model`() {
    val entity = NowPlayingEntity(
      title = "Song",
      artist = "Artist",
      path = "path",
      position = 0,
      dateAdded = 123456789L,
      id = 1
    )
    val nowPlaying = entity.toNowPlaying()
    // NowPlaying domain model does not have dateAdded
    assertThat(nowPlaying.id).isEqualTo(1)
    assertThat(nowPlaying.title).isEqualTo("Song")
  }

  // endregion

  // region Object mapper tests

  @Test
  fun `NowPlayingDtoMapper map should work correctly`() {
    val dto =
      NowPlayingDto(title = "Test Song", artist = "Test Artist", path = "test.mp3", position = 3)
    val entity = NowPlayingDtoMapper.map(dto)
    assertThat(entity.title).isEqualTo("Test Song")
    assertThat(entity.artist).isEqualTo("Test Artist")
    assertThat(entity.path).isEqualTo("test.mp3")
    assertThat(entity.position).isEqualTo(3)
  }

  @Test
  fun `NowPlayingEntityMapper map should work correctly`() {
    val entity = createNowPlayingEntity(
      title = "Another Song",
      artist = "Another Artist",
      path = "another.mp3",
      position = 10,
      id = 5
    )
    val nowPlaying = NowPlayingEntityMapper.map(entity)
    assertThat(nowPlaying.title).isEqualTo("Another Song")
    assertThat(nowPlaying.artist).isEqualTo("Another Artist")
    assertThat(nowPlaying.path).isEqualTo("another.mp3")
    assertThat(nowPlaying.position).isEqualTo(10)
    assertThat(nowPlaying.id).isEqualTo(5)
  }

  // endregion

  // Helper function
  private fun createNowPlayingEntity(
    title: String = "Title",
    artist: String = "Artist",
    path: String = "path/to/file.mp3",
    position: Int = 0,
    id: Long = 0
  ) = NowPlayingEntity(
    title = title,
    artist = artist,
    path = path,
    position = position,
    id = id
  )
}
