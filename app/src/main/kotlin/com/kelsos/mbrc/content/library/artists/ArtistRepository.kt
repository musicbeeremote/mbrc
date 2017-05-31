package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.Repository
import com.raizlabs.android.dbflow.list.FlowCursorList

interface ArtistRepository : Repository<Artist> {
  suspend fun getArtistByGenre(genre: String): FlowCursorList<Artist>
  suspend fun getAlbumArtistsOnly(): FlowCursorList<Artist>
  suspend fun getAllRemoteAndShowAlbumArtist(): FlowCursorList<Artist>
}
