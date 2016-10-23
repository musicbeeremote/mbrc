package com.kelsos.mbrc.interactors.library

import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.mappers.ArtistMapper
import com.kelsos.mbrc.repository.library.ArtistRepository
import rx.Observable
import rx.lang.kotlin.toSingletonObservable
import javax.inject.Inject

class GenreArtistInteractorImpl
@Inject constructor(private val repository: ArtistRepository) : GenreArtistInteractor {

  override fun getGenreArtists(genreId: Long): Observable<List<Artist>> {
    return repository.getArtistsByGenreId(genreId)
        .flatMap {
          ArtistMapper.mapGenreArtists(it).toSingletonObservable()
        }
  }
}
