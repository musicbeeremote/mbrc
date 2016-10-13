package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Artist
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single

interface ArtistRepository : Repository<Artist> {
  fun getArtistByGenre(genre: String): Single<FlowCursorList<Artist>>
}
