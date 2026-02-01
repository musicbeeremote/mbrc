package com.kelsos.mbrc.feature.library.genres

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.data.library.genre.GenreEntity
import com.kelsos.mbrc.core.networking.dto.GenreDto
import org.junit.Test

class GenreMappersTest {

  // region GenreDto to GenreEntity mapping

  @Test
  fun `toEntity should map genre name from dto`() {
    val dto = GenreDto(genre = "Progressive Rock")
    val entity = dto.toEntity()
    assertThat(entity.genre).isEqualTo("Progressive Rock")
  }

  @Test
  fun `toEntity should handle empty genre name`() {
    val dto = GenreDto(genre = "")
    val entity = dto.toEntity()
    assertThat(entity.genre).isEmpty()
  }

  @Test
  fun `toEntity should handle special characters`() {
    val dto = GenreDto(genre = "Rock & Roll")
    val entity = dto.toEntity()
    assertThat(entity.genre).isEqualTo("Rock & Roll")
  }

  @Test
  fun `toEntity should handle unicode characters`() {
    val dto = GenreDto(genre = "Música")
    val entity = dto.toEntity()
    assertThat(entity.genre).isEqualTo("Música")
  }

  @Test
  fun `toEntity should handle long genre names`() {
    val longName = "G".repeat(200)
    val dto = GenreDto(genre = longName)
    val entity = dto.toEntity()
    assertThat(entity.genre).isEqualTo(longName)
  }

  @Test
  fun `toEntity should handle genre with slashes`() {
    val dto = GenreDto(genre = "Pop/Rock")
    val entity = dto.toEntity()
    assertThat(entity.genre).isEqualTo("Pop/Rock")
  }

  @Test
  fun `toEntity should not preserve count from dto`() {
    val dto = GenreDto(genre = "Jazz", count = 150)
    val entity = dto.toEntity()
    // Entity does not have count field
    assertThat(entity.genre).isEqualTo("Jazz")
  }

  @Test
  fun `toEntity should set default id to 0`() {
    val dto = GenreDto(genre = "Test")
    val entity = dto.toEntity()
    assertThat(entity.id).isEqualTo(0)
  }

  @Test
  fun `toEntity should set default dateAdded to 0`() {
    val dto = GenreDto(genre = "Test")
    val entity = dto.toEntity()
    assertThat(entity.dateAdded).isEqualTo(0)
  }

  // endregion

  // region GenreEntity to Genre mapping

  @Test
  fun `toGenre should map id from entity`() {
    val entity = GenreEntity(genre = "Test", id = 42)
    val genre = entity.toGenre()
    assertThat(genre.id).isEqualTo(42)
  }

  @Test
  fun `toGenre should map genre name from entity`() {
    val entity = GenreEntity(genre = "Electronic")
    val genre = entity.toGenre()
    assertThat(genre.genre).isEqualTo("Electronic")
  }

  @Test
  fun `toGenre should handle empty genre name`() {
    val entity = GenreEntity(genre = "")
    val genre = entity.toGenre()
    assertThat(genre.genre).isEmpty()
  }

  @Test
  fun `toGenre should handle entity with default id`() {
    val entity = GenreEntity(genre = "Blues")
    val genre = entity.toGenre()
    assertThat(genre.id).isEqualTo(0)
  }

  @Test
  fun `toGenre should not include dateAdded in domain model`() {
    val entity = GenreEntity(genre = "Test", dateAdded = 123456789L, id = 1)
    val genre = entity.toGenre()
    // Genre domain model only has genre and id fields
    assertThat(genre.id).isEqualTo(1)
    assertThat(genre.genre).isEqualTo("Test")
  }

  // endregion

  // region Object mapper tests

  @Test
  fun `GenreDtoMapper map should work correctly`() {
    val dto = GenreDto(genre = "Heavy Metal")
    val entity = GenreDtoMapper.map(dto)
    assertThat(entity.genre).isEqualTo("Heavy Metal")
  }

  @Test
  fun `GenreEntityMapper map should work correctly`() {
    val entity = GenreEntity(genre = "Classical", id = 10)
    val genre = GenreEntityMapper.map(entity)
    assertThat(genre.genre).isEqualTo("Classical")
    assertThat(genre.id).isEqualTo(10)
  }

  // endregion
}
