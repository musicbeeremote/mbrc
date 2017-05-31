package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.LocalDataSource
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Single

interface LocalArtistDataSource : LocalDataSource<Artist> {
  fun getArtistByGenre(genre: String): Single<FlowCursorList<Artist>>
  fun getAlbumArtists(): Single<FlowCursorList<Artist>>
}
