package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.dao.ArtistDao
import com.kelsos.mbrc.dao.views.GenreArtistView
import com.kelsos.mbrc.repository.Repository
import rx.Observable

interface ArtistRepository : Repository<ArtistDao> {
    fun getArtistsByGenreId(id: Long): Observable<List<GenreArtistView>>
}
