package com.kelsos.mbrc.core.common.settings

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LibrarySortingTest {

  // region SortOrder tests

  @Test
  fun `SortOrder fromString with valid asc`() {
    assertThat(SortOrder.fromString("asc")).isEqualTo(SortOrder.ASC)
  }

  @Test
  fun `SortOrder fromString with valid desc`() {
    assertThat(SortOrder.fromString("desc")).isEqualTo(SortOrder.DESC)
  }

  @Test
  fun `SortOrder fromString with invalid falls back to ASC`() {
    assertThat(SortOrder.fromString("invalid")).isEqualTo(SortOrder.ASC)
  }

  // endregion

  // region GenreSortField tests

  @Test
  fun `GenreSortField fromString with valid name`() {
    assertThat(GenreSortField.fromString("name")).isEqualTo(GenreSortField.NAME)
  }

  @Test
  fun `GenreSortField fromString with invalid falls back to NAME`() {
    assertThat(GenreSortField.fromString("invalid")).isEqualTo(GenreSortField.NAME)
  }

  // endregion

  // region ArtistSortField tests

  @Test
  fun `ArtistSortField fromString with valid name`() {
    assertThat(ArtistSortField.fromString("name")).isEqualTo(ArtistSortField.NAME)
  }

  @Test
  fun `ArtistSortField fromString with invalid falls back to NAME`() {
    assertThat(ArtistSortField.fromString("invalid")).isEqualTo(ArtistSortField.NAME)
  }

  // endregion

  // region AlbumSortField tests

  @Test
  fun `AlbumSortField fromString with valid name`() {
    assertThat(AlbumSortField.fromString("name")).isEqualTo(AlbumSortField.NAME)
  }

  @Test
  fun `AlbumSortField fromString with valid artist`() {
    assertThat(AlbumSortField.fromString("artist")).isEqualTo(AlbumSortField.ARTIST)
  }

  @Test
  fun `AlbumSortField fromString with invalid falls back to NAME`() {
    assertThat(AlbumSortField.fromString("invalid")).isEqualTo(AlbumSortField.NAME)
  }

  // endregion

  // region TrackSortField tests

  @Test
  fun `TrackSortField fromString with valid title`() {
    assertThat(TrackSortField.fromString("title")).isEqualTo(TrackSortField.TITLE)
  }

  @Test
  fun `TrackSortField fromString with valid artist`() {
    assertThat(TrackSortField.fromString("artist")).isEqualTo(TrackSortField.ARTIST)
  }

  @Test
  fun `TrackSortField fromString with valid album`() {
    assertThat(TrackSortField.fromString("album")).isEqualTo(TrackSortField.ALBUM)
  }

  @Test
  fun `TrackSortField fromString with valid album_artist`() {
    assertThat(TrackSortField.fromString("album_artist")).isEqualTo(TrackSortField.ALBUM_ARTIST)
  }

  @Test
  fun `TrackSortField fromString with invalid falls back to TITLE`() {
    assertThat(TrackSortField.fromString("invalid")).isEqualTo(TrackSortField.TITLE)
  }

  // endregion

  // region SortPreference tests

  @Test
  fun `SortPreference encode produces correct format`() {
    val preference = SortPreference(AlbumSortField.ARTIST, SortOrder.DESC)
    val encoded = SortPreference.encode(preference) { it.value }
    assertThat(encoded).isEqualTo("artist:desc")
  }

  @Test
  fun `SortPreference decode with valid input`() {
    val decoded = SortPreference.decode(
      "artist:desc",
      AlbumSortField::fromString,
      AlbumSortField.NAME
    )
    assertThat(decoded.field).isEqualTo(AlbumSortField.ARTIST)
    assertThat(decoded.order).isEqualTo(SortOrder.DESC)
  }

  @Test
  fun `SortPreference decode with malformed input falls back to default`() {
    val decoded = SortPreference.decode(
      "malformed",
      AlbumSortField::fromString,
      AlbumSortField.NAME
    )
    assertThat(decoded.field).isEqualTo(AlbumSortField.NAME)
    assertThat(decoded.order).isEqualTo(SortOrder.ASC)
  }

  @Test
  fun `SortPreference decode with empty string falls back to default`() {
    val decoded = SortPreference.decode(
      "",
      AlbumSortField::fromString,
      AlbumSortField.NAME
    )
    assertThat(decoded.field).isEqualTo(AlbumSortField.NAME)
    assertThat(decoded.order).isEqualTo(SortOrder.ASC)
  }

  @Test
  fun `SortPreference encode then decode round-trip`() {
    val original = SortPreference(TrackSortField.ALBUM_ARTIST, SortOrder.DESC)
    val encoded = SortPreference.encode(original) { it.value }
    val decoded = SortPreference.decode(
      encoded,
      TrackSortField::fromString,
      TrackSortField.TITLE
    )
    assertThat(decoded).isEqualTo(original)
  }

  // endregion
}
