package com.kelsos.mbrc.feature.content.playlists

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.data.playlist.PlaylistEntity
import com.kelsos.mbrc.core.networking.dto.PlaylistDto
import org.junit.Test

class PlaylistMappersTest {

  // region PlaylistDto to PlaylistEntity mapping

  @Test
  fun `toEntity should map name from dto`() {
    val dto = PlaylistDto(name = "My Favorites", url = "playlist://1")
    val entity = dto.toEntity()
    assertThat(entity.name).isEqualTo("My Favorites")
  }

  @Test
  fun `toEntity should map url from dto`() {
    val dto = PlaylistDto(name = "My Favorites", url = "playlist://1")
    val entity = dto.toEntity()
    assertThat(entity.url).isEqualTo("playlist://1")
  }

  @Test
  fun `toEntity should handle empty name`() {
    val dto = PlaylistDto(name = "", url = "playlist://1")
    val entity = dto.toEntity()
    assertThat(entity.name).isEmpty()
  }

  @Test
  fun `toEntity should handle empty url`() {
    val dto = PlaylistDto(name = "Test", url = "")
    val entity = dto.toEntity()
    assertThat(entity.url).isEmpty()
  }

  @Test
  fun `toEntity should handle special characters in name`() {
    val dto = PlaylistDto(name = "Rock & Roll (2023)", url = "playlist://1")
    val entity = dto.toEntity()
    assertThat(entity.name).isEqualTo("Rock & Roll (2023)")
  }

  @Test
  fun `toEntity should handle unicode characters`() {
    val dto = PlaylistDto(name = "Música Latina", url = "playlist://1")
    val entity = dto.toEntity()
    assertThat(entity.name).isEqualTo("Música Latina")
  }

  @Test
  fun `toEntity should handle long names`() {
    val longName = "P".repeat(500)
    val dto = PlaylistDto(name = longName, url = "playlist://1")
    val entity = dto.toEntity()
    assertThat(entity.name).isEqualTo(longName)
  }

  @Test
  fun `toEntity should set default id to 0`() {
    val dto = PlaylistDto(name = "Test", url = "test")
    val entity = dto.toEntity()
    assertThat(entity.id).isEqualTo(0)
  }

  @Test
  fun `toEntity should set default dateAdded to 0`() {
    val dto = PlaylistDto(name = "Test", url = "test")
    val entity = dto.toEntity()
    assertThat(entity.dateAdded).isEqualTo(0)
  }

  @Test
  fun `toEntity should handle default dto values`() {
    val dto = PlaylistDto()
    val entity = dto.toEntity()
    assertThat(entity.name).isEmpty()
    assertThat(entity.url).isEmpty()
  }

  // endregion

  // region PlaylistEntity to Playlist mapping

  @Test
  fun `toPlaylist should map id from entity`() {
    val entity = PlaylistEntity(name = "Test", url = "test", id = 42)
    val playlist = entity.toPlaylist()
    assertThat(playlist.id).isEqualTo(42)
  }

  @Test
  fun `toPlaylist should map name from entity`() {
    val entity = PlaylistEntity(name = "Workout Mix", url = "playlist://1")
    val playlist = entity.toPlaylist()
    assertThat(playlist.name).isEqualTo("Workout Mix")
  }

  @Test
  fun `toPlaylist should map url from entity`() {
    val entity = PlaylistEntity(name = "Test", url = "playlist://abc")
    val playlist = entity.toPlaylist()
    assertThat(playlist.url).isEqualTo("playlist://abc")
  }

  @Test
  fun `toPlaylist should handle empty strings`() {
    val entity = PlaylistEntity(name = "", url = "")
    val playlist = entity.toPlaylist()
    assertThat(playlist.name).isEmpty()
    assertThat(playlist.url).isEmpty()
  }

  @Test
  fun `toPlaylist should handle entity with default id`() {
    val entity = PlaylistEntity(name = "Test", url = "test")
    val playlist = entity.toPlaylist()
    assertThat(playlist.id).isEqualTo(0)
  }

  @Test
  fun `toPlaylist should not include dateAdded in domain model`() {
    val entity = PlaylistEntity(name = "Test", url = "test", dateAdded = 123456789L, id = 1)
    val playlist = entity.toPlaylist()
    assertThat(playlist.id).isEqualTo(1)
    assertThat(playlist.name).isEqualTo("Test")
    assertThat(playlist.url).isEqualTo("test")
  }

  // endregion

  // region Object mapper tests

  @Test
  fun `PlaylistDtoMapper map should work correctly`() {
    val dto = PlaylistDto(name = "Summer Hits", url = "playlist://summer")
    val entity = PlaylistDtoMapper.map(dto)
    assertThat(entity.name).isEqualTo("Summer Hits")
    assertThat(entity.url).isEqualTo("playlist://summer")
  }

  @Test
  fun `PlaylistEntityMapper map should work correctly`() {
    val entity = PlaylistEntity(name = "Party Mix", url = "playlist://party", id = 10)
    val playlist = PlaylistEntityMapper.map(entity)
    assertThat(playlist.name).isEqualTo("Party Mix")
    assertThat(playlist.url).isEqualTo("playlist://party")
    assertThat(playlist.id).isEqualTo(10)
  }

  // endregion
}
