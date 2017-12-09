package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.Repository

interface ArtistRepository : Repository<Artist> {
  suspend fun getArtistByGenre(genre: String): List<Artist>
  suspend fun getAlbumArtistsOnly(): List<Artist>
  suspend fun getAllRemoteAndShowAlbumArtist(): List<Artist>
}
