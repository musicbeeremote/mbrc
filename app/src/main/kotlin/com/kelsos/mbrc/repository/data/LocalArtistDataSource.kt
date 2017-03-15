package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.library.Artist
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Single

interface LocalArtistDataSource : LocalDataSource<Artist> {
  fun getArtistByGenre(genre: String): Single<FlowCursorList<Artist>>
  fun getAlbumArtists(): Single<FlowCursorList<Artist>>
}
