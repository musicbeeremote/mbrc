package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.LocalDataSource

interface LocalArtistDataSource : LocalDataSource<Artist> {
  suspend fun getArtistByGenre(genre: String): List<Artist>
  suspend fun getAlbumArtists(): List<Artist>
}
