package com.kelsos.mbrc.feature.library.albums

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.data.library.album.AlbumEntity
import com.kelsos.mbrc.core.networking.dto.AlbumDto
import org.junit.Test

class AlbumMappersTest {

  // region AlbumDto to AlbumEntity mapping

  @Test
  fun `toEntity should map artist from dto`() {
    val dto = AlbumDto(artist = "Pink Floyd", album = "The Wall")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("Pink Floyd")
  }

  @Test
  fun `toEntity should map album from dto`() {
    val dto = AlbumDto(artist = "Pink Floyd", album = "The Wall")
    val entity = dto.toEntity()
    assertThat(entity.album).isEqualTo("The Wall")
  }

  @Test
  fun `toEntity should handle empty artist`() {
    val dto = AlbumDto(artist = "", album = "Album")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEmpty()
  }

  @Test
  fun `toEntity should handle empty album`() {
    val dto = AlbumDto(artist = "Artist", album = "")
    val entity = dto.toEntity()
    assertThat(entity.album).isEmpty()
  }

  @Test
  fun `toEntity should handle special characters`() {
    val dto = AlbumDto(artist = "AC/DC", album = "Back in Black (Remastered)")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("AC/DC")
    assertThat(entity.album).isEqualTo("Back in Black (Remastered)")
  }

  @Test
  fun `toEntity should handle unicode characters`() {
    val dto = AlbumDto(artist = "Sigur Rós", album = "( )")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("Sigur Rós")
    assertThat(entity.album).isEqualTo("( )")
  }

  @Test
  fun `toEntity should set cover to null by default`() {
    val dto = AlbumDto(artist = "Test", album = "Test Album")
    val entity = dto.toEntity()
    assertThat(entity.cover).isNull()
  }

  @Test
  fun `toEntity should set default id to 0`() {
    val dto = AlbumDto(artist = "Test", album = "Test")
    val entity = dto.toEntity()
    assertThat(entity.id).isEqualTo(0)
  }

  @Test
  fun `toEntity should set default dateAdded to 0`() {
    val dto = AlbumDto(artist = "Test", album = "Test")
    val entity = dto.toEntity()
    assertThat(entity.dateAdded).isEqualTo(0)
  }

  @Test
  fun `toEntity should not preserve count from dto`() {
    val dto = AlbumDto(artist = "Test", album = "Test", count = 15)
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("Test")
    assertThat(entity.album).isEqualTo("Test")
  }

  // endregion

  // region AlbumEntity to Album mapping

  @Test
  fun `toAlbum should map id from entity`() {
    val entity = AlbumEntity(artist = "Test", album = "Test", id = 42)
    val album = entity.toAlbum()
    assertThat(album.id).isEqualTo(42)
  }

  @Test
  fun `toAlbum should map artist from entity`() {
    val entity = AlbumEntity(artist = "The Beatles", album = "Abbey Road")
    val album = entity.toAlbum()
    assertThat(album.artist).isEqualTo("The Beatles")
  }

  @Test
  fun `toAlbum should map album name from entity`() {
    val entity = AlbumEntity(artist = "The Beatles", album = "Abbey Road")
    val album = entity.toAlbum()
    assertThat(album.album).isEqualTo("Abbey Road")
  }

  @Test
  fun `toAlbum should map cover from entity when present`() {
    val entity = AlbumEntity(artist = "Test", album = "Test", cover = "abc123")
    val album = entity.toAlbum()
    assertThat(album.cover).isEqualTo("abc123")
  }

  @Test
  fun `toAlbum should map null cover from entity`() {
    val entity = AlbumEntity(artist = "Test", album = "Test", cover = null)
    val album = entity.toAlbum()
    assertThat(album.cover).isNull()
  }

  @Test
  fun `toAlbum should handle empty strings`() {
    val entity = AlbumEntity(artist = "", album = "")
    val album = entity.toAlbum()
    assertThat(album.artist).isEmpty()
    assertThat(album.album).isEmpty()
  }

  @Test
  fun `toAlbum should not include dateAdded in domain model`() {
    val entity = AlbumEntity(artist = "Test", album = "Test", dateAdded = 123456789L, id = 1)
    val album = entity.toAlbum()
    assertThat(album.id).isEqualTo(1)
    assertThat(album.artist).isEqualTo("Test")
    assertThat(album.album).isEqualTo("Test")
  }

  // endregion

  // region Object mapper tests

  @Test
  fun `AlbumDtoMapper map should work correctly`() {
    val dto = AlbumDto(artist = "Metallica", album = "Master of Puppets")
    val entity = AlbumDtoMapper.map(dto)
    assertThat(entity.artist).isEqualTo("Metallica")
    assertThat(entity.album).isEqualTo("Master of Puppets")
  }

  @Test
  fun `AlbumEntityMapper map should work correctly`() {
    val entity = AlbumEntity(artist = "Iron Maiden", album = "Powerslave", id = 10, cover = "xyz")
    val album = AlbumEntityMapper.map(entity)
    assertThat(album.artist).isEqualTo("Iron Maiden")
    assertThat(album.album).isEqualTo("Powerslave")
    assertThat(album.id).isEqualTo(10)
    assertThat(album.cover).isEqualTo("xyz")
  }

  // endregion
}
