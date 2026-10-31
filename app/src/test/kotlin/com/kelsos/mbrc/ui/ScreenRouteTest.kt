package com.kelsos.mbrc.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.google.common.truth.Truth.assertWithMessage
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenRouteTest {
  @Test
  fun `album tracks routes encode the album and artist`() {
    assertWithMessage("a slash in a name would otherwise add a path segment the route cannot match")
      .that(Screen.AlbumTracks(5, "Back/Forth", "AC/DC").route)
      .isEqualTo("album_tracks/5/Back%2FForth/AC%2FDC")
  }

  @Test
  fun `artist albums routes encode the artist`() {
    assertThat(Screen.ArtistAlbums(0, "Simon & Garfunkel").route)
      .isEqualTo("artist_albums/0/Simon%20%26%20Garfunkel")
  }

  @Test
  fun `genre routes encode the genre`() {
    assertThat(Screen.GenreArtists(3, "Drum & Bass").route)
      .isEqualTo("genre_artists/3/Drum%20%26%20Bass")
    assertThat(Screen.GenreAlbums(3, "Hip/Hop").route).isEqualTo("genre_albums/3/Hip%2FHop")
  }
}
