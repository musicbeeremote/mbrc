package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.repository.Repository
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single

interface ArtistRepository : Repository<Artist> {
  fun getArtistByGenre(genre: String): Single<FlowCursorList<Artist>>
}
