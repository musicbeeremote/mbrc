package com.kelsos.mbrc.feature.library.artists

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.data.library.artist.ArtistEntity
import com.kelsos.mbrc.core.networking.dto.ArtistDto
import org.junit.Test

class ArtistMappersTest {

  // region ArtistDto to ArtistEntity mapping

  @Test
  fun `toEntity should map artist name from dto`() {
    val dto = ArtistDto(artist = "Pink Floyd")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("Pink Floyd")
  }

  @Test
  fun `toEntity should handle empty artist name`() {
    val dto = ArtistDto(artist = "")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEmpty()
  }

  @Test
  fun `toEntity should handle special characters in artist name`() {
    val dto = ArtistDto(artist = "AC/DC")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("AC/DC")
  }

  @Test
  fun `toEntity should handle unicode characters`() {
    val dto = ArtistDto(artist = "Björk")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("Björk")
  }

  @Test
  fun `toEntity should handle long artist names`() {
    val longName = "A".repeat(500)
    val dto = ArtistDto(artist = longName)
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo(longName)
  }

  @Test
  fun `toEntity should not preserve count from dto`() {
    val dto = ArtistDto(artist = "Test", count = 42)
    val entity = dto.toEntity()
    // Entity does not have a count field
    assertThat(entity.artist).isEqualTo("Test")
  }

  @Test
  fun `toEntity should set default id to 0`() {
    val dto = ArtistDto(artist = "Test")
    val entity = dto.toEntity()
    assertThat(entity.id).isEqualTo(0)
  }

  @Test
  fun `toEntity should set default dateAdded to 0`() {
    val dto = ArtistDto(artist = "Test")
    val entity = dto.toEntity()
    assertThat(entity.dateAdded).isEqualTo(0)
  }

  // endregion

  // region ArtistEntity to Artist mapping

  @Test
  fun `toArtist should map id from entity`() {
    val entity = ArtistEntity(artist = "Test", id = 42)
    val artist = entity.toArtist()
    assertThat(artist.id).isEqualTo(42)
  }

  @Test
  fun `toArtist should map artist name from entity`() {
    val entity = ArtistEntity(artist = "The Beatles")
    val artist = entity.toArtist()
    assertThat(artist.artist).isEqualTo("The Beatles")
  }

  @Test
  fun `toArtist should handle empty artist name`() {
    val entity = ArtistEntity(artist = "")
    val artist = entity.toArtist()
    assertThat(artist.artist).isEmpty()
  }

  @Test
  fun `toArtist should not include dateAdded in domain model`() {
    val entity = ArtistEntity(artist = "Test", dateAdded = 123456789L, id = 1)
    val artist = entity.toArtist()
    // Artist domain model only has id and artist fields
    assertThat(artist.id).isEqualTo(1)
    assertThat(artist.artist).isEqualTo("Test")
  }

  // endregion

  // region Object mapper tests

  @Test
  fun `ArtistDtoMapper map should work correctly`() {
    val dto = ArtistDto(artist = "Metallica")
    val entity = ArtistDtoMapper.map(dto)
    assertThat(entity.artist).isEqualTo("Metallica")
  }

  @Test
  fun `ArtistEntityMapper map should work correctly`() {
    val entity = ArtistEntity(artist = "Iron Maiden", id = 10)
    val artist = ArtistEntityMapper.map(entity)
    assertThat(artist.artist).isEqualTo("Iron Maiden")
    assertThat(artist.id).isEqualTo(10)
  }

  // endregion
}
