package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.LocalDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList

interface LocalArtistDataSource : LocalDataSource<Artist> {
  suspend fun getArtistByGenre(genre: String): FlowCursorList<Artist>
  suspend fun getAlbumArtists(): FlowCursorList<Artist>
}
