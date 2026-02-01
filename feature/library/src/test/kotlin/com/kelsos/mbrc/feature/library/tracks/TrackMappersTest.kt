package com.kelsos.mbrc.feature.library.tracks

import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.core.data.library.track.TrackEntity
import com.kelsos.mbrc.core.networking.dto.TrackDto
import org.junit.Test

class TrackMappersTest {

  // region TrackDto to TrackEntity mapping - Basic fields

  @Test
  fun `toEntity should map artist from dto`() {
    val dto = createTrackDto(artist = "Pink Floyd")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("Pink Floyd")
  }

  @Test
  fun `toEntity should map title from dto`() {
    val dto = createTrackDto(title = "Comfortably Numb")
    val entity = dto.toEntity()
    assertThat(entity.title).isEqualTo("Comfortably Numb")
  }

  @Test
  fun `toEntity should map src from dto`() {
    val dto = createTrackDto(src = "C:\\Music\\song.mp3")
    val entity = dto.toEntity()
    assertThat(entity.src).isEqualTo("C:\\Music\\song.mp3")
  }

  @Test
  fun `toEntity should map trackno from dto`() {
    val dto = createTrackDto(trackno = 5)
    val entity = dto.toEntity()
    assertThat(entity.trackno).isEqualTo(5)
  }

  @Test
  fun `toEntity should map disc from dto`() {
    val dto = createTrackDto(disc = 2)
    val entity = dto.toEntity()
    assertThat(entity.disc).isEqualTo(2)
  }

  @Test
  fun `toEntity should map albumArtist from dto`() {
    val dto = createTrackDto(albumArtist = "Various Artists")
    val entity = dto.toEntity()
    assertThat(entity.albumArtist).isEqualTo("Various Artists")
  }

  @Test
  fun `toEntity should map album from dto`() {
    val dto = createTrackDto(album = "The Wall")
    val entity = dto.toEntity()
    assertThat(entity.album).isEqualTo("The Wall")
  }

  @Test
  fun `toEntity should map genre from dto`() {
    val dto = createTrackDto(genre = "Progressive Rock")
    val entity = dto.toEntity()
    assertThat(entity.genre).isEqualTo("Progressive Rock")
  }

  @Test
  fun `toEntity should map year from dto`() {
    val dto = createTrackDto(year = "1979")
    val entity = dto.toEntity()
    assertThat(entity.year).isEqualTo("1979")
  }

  // endregion

  // region Year parsing tests

  @Test
  fun `toEntity should parse sortableYear from standard year`() {
    val dto = createTrackDto(year = "1979")
    val entity = dto.toEntity()
    assertThat(entity.sortableYear).isEqualTo("1979")
  }

  @Test
  fun `toEntity should parse sortableYear from date format`() {
    val dto = createTrackDto(year = "2023-05-15")
    val entity = dto.toEntity()
    assertThat(entity.sortableYear).isEqualTo("2023")
  }

  @Test
  fun `toEntity should parse sortableYear from year with text`() {
    val dto = createTrackDto(year = "Released in 1985")
    val entity = dto.toEntity()
    assertThat(entity.sortableYear).isEqualTo("1985")
  }

  @Test
  fun `toEntity should parse sortableYear from year at end`() {
    val dto = createTrackDto(year = "Remastered 2020")
    val entity = dto.toEntity()
    assertThat(entity.sortableYear).isEqualTo("2020")
  }

  @Test
  fun `toEntity should return empty sortableYear for empty year`() {
    val dto = createTrackDto(year = "")
    val entity = dto.toEntity()
    assertThat(entity.sortableYear).isEmpty()
  }

  @Test
  fun `toEntity should return empty sortableYear for non-year text`() {
    val dto = createTrackDto(year = "Unknown")
    val entity = dto.toEntity()
    assertThat(entity.sortableYear).isEmpty()
  }

  @Test
  fun `toEntity should return empty sortableYear for 3 digit number`() {
    val dto = createTrackDto(year = "123")
    val entity = dto.toEntity()
    assertThat(entity.sortableYear).isEmpty()
  }

  @Test
  fun `toEntity should handle year with surrounding spaces`() {
    val dto = createTrackDto(year = " 1990 ")
    val entity = dto.toEntity()
    assertThat(entity.sortableYear).isEqualTo("1990")
  }

  @Test
  fun `toEntity should take last 4-digit number when multiple present due to greedy regex`() {
    val dto = createTrackDto(year = "1985-1990")
    val entity = dto.toEntity()
    // The regex is greedy, so it matches the last 4-digit number
    assertThat(entity.sortableYear).isEqualTo("1990")
  }

  // endregion

  // region Edge cases

  @Test
  fun `toEntity should handle empty fields`() {
    val dto = TrackDto()
    val entity = dto.toEntity()
    assertThat(entity.artist).isEmpty()
    assertThat(entity.title).isEmpty()
    assertThat(entity.src).isEmpty()
    assertThat(entity.trackno).isEqualTo(0)
    assertThat(entity.disc).isEqualTo(0)
    assertThat(entity.albumArtist).isEmpty()
    assertThat(entity.album).isEmpty()
    assertThat(entity.genre).isEmpty()
    assertThat(entity.year).isEmpty()
    assertThat(entity.sortableYear).isEmpty()
  }

  @Test
  fun `toEntity should handle special characters`() {
    val dto = createTrackDto(
      artist = "AC/DC",
      title = "It's a Long Way to the Top (If You Wanna Rock 'n' Roll)"
    )
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("AC/DC")
    assertThat(entity.title).isEqualTo("It's a Long Way to the Top (If You Wanna Rock 'n' Roll)")
  }

  @Test
  fun `toEntity should handle unicode characters`() {
    val dto = createTrackDto(artist = "Sigur Rós", title = "Glósóli")
    val entity = dto.toEntity()
    assertThat(entity.artist).isEqualTo("Sigur Rós")
    assertThat(entity.title).isEqualTo("Glósóli")
  }

  @Test
  fun `toEntity should set default id to 0`() {
    val dto = createTrackDto()
    val entity = dto.toEntity()
    assertThat(entity.id).isEqualTo(0)
  }

  @Test
  fun `toEntity should set default dateAdded to 0`() {
    val dto = createTrackDto()
    val entity = dto.toEntity()
    assertThat(entity.dateAdded).isEqualTo(0)
  }

  // endregion

  // region TrackEntity to Track mapping

  @Test
  fun `toTrack should map all fields from entity`() {
    val entity = TrackEntity(
      artist = "Pink Floyd",
      title = "Wish You Were Here",
      src = "C:\\Music\\song.mp3",
      trackno = 1,
      disc = 1,
      albumArtist = "Pink Floyd",
      album = "Wish You Were Here",
      genre = "Rock",
      year = "1975",
      sortableYear = "1975",
      id = 42
    )
    val track = entity.toTrack()

    assertThat(track.artist).isEqualTo("Pink Floyd")
    assertThat(track.title).isEqualTo("Wish You Were Here")
    assertThat(track.src).isEqualTo("C:\\Music\\song.mp3")
    assertThat(track.trackno).isEqualTo(1)
    assertThat(track.disc).isEqualTo(1)
    assertThat(track.albumArtist).isEqualTo("Pink Floyd")
    assertThat(track.album).isEqualTo("Wish You Were Here")
    assertThat(track.genre).isEqualTo("Rock")
    assertThat(track.year).isEqualTo("1975")
    assertThat(track.id).isEqualTo(42)
  }

  @Test
  fun `toTrack should handle entity with default id`() {
    val entity = createTrackEntity()
    val track = entity.toTrack()
    assertThat(track.id).isEqualTo(0)
  }

  @Test
  fun `toTrack should not include sortableYear in domain model`() {
    val entity = TrackEntity(
      artist = "Test",
      title = "Test",
      src = "test.mp3",
      trackno = 1,
      disc = 1,
      albumArtist = "Test",
      album = "Test",
      genre = "Rock",
      year = "2020",
      sortableYear = "2020",
      id = 1
    )
    val track = entity.toTrack()
    // Track domain model does not have sortableYear field
    assertThat(track.year).isEqualTo("2020")
  }

  @Test
  fun `toTrack should not include dateAdded in domain model`() {
    val entity = TrackEntity(
      artist = "Test",
      title = "Test",
      src = "test.mp3",
      trackno = 1,
      disc = 1,
      albumArtist = "Test",
      album = "Test",
      genre = "Rock",
      year = "2020",
      sortableYear = "2020",
      dateAdded = 123456789L,
      id = 1
    )
    val track = entity.toTrack()
    assertThat(track.id).isEqualTo(1)
  }

  // endregion

  // region Object mapper tests

  @Test
  fun `TrackDtoMapper map should work correctly`() {
    val dto = createTrackDto(artist = "Metallica", title = "Enter Sandman")
    val entity = TrackDtoMapper.map(dto)
    assertThat(entity.artist).isEqualTo("Metallica")
    assertThat(entity.title).isEqualTo("Enter Sandman")
  }

  @Test
  fun `TrackEntityMapper map should work correctly`() {
    val entity = createTrackEntity(artist = "Iron Maiden", title = "Hallowed Be Thy Name", id = 10)
    val track = TrackEntityMapper.map(entity)
    assertThat(track.artist).isEqualTo("Iron Maiden")
    assertThat(track.title).isEqualTo("Hallowed Be Thy Name")
    assertThat(track.id).isEqualTo(10)
  }

  // endregion

  // Helper functions
  private fun createTrackDto(
    artist: String = "Artist",
    title: String = "Title",
    src: String = "path/to/file.mp3",
    trackno: Int = 1,
    disc: Int = 1,
    albumArtist: String = "Album Artist",
    album: String = "Album",
    genre: String = "Genre",
    year: String = "2020"
  ) = TrackDto(
    artist = artist,
    title = title,
    src = src,
    trackno = trackno,
    disc = disc,
    albumArtist = albumArtist,
    album = album,
    genre = genre,
    year = year
  )

  private fun createTrackEntity(
    artist: String = "Artist",
    title: String = "Title",
    src: String = "path/to/file.mp3",
    trackno: Int = 1,
    disc: Int = 1,
    albumArtist: String = "Album Artist",
    album: String = "Album",
    genre: String = "Genre",
    year: String = "2020",
    sortableYear: String = "2020",
    id: Long = 0
  ) = TrackEntity(
    artist = artist,
    title = title,
    src = src,
    trackno = trackno,
    disc = disc,
    albumArtist = albumArtist,
    album = album,
    genre = genre,
    year = year,
    sortableYear = sortableYear,
    id = id
  )
}
