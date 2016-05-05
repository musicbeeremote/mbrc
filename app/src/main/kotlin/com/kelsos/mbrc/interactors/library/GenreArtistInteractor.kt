package com.kelsos.mbrc.interactors.library

import com.kelsos.mbrc.domain.Artist
import rx.Observable

interface GenreArtistInteractor {
  fun getGenreArtists(genreId: Long): Observable<List<Artist>>
}
