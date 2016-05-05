package com.kelsos.mbrc.interactors.library

import com.google.inject.Inject
import com.kelsos.mbrc.constants.Constants
import com.kelsos.mbrc.domain.Genre
import com.kelsos.mbrc.extensions.io
import com.kelsos.mbrc.mappers.GenreMapper
import com.kelsos.mbrc.repository.library.GenreRepository
import rx.Observable
import rx.lang.kotlin.toSingletonObservable

class LibraryGenreInteractor {
  @Inject private lateinit var repository: GenreRepository

  fun execute(offset: Int = 0, limit: Int = Constants.PAGE_SIZE): Observable<List<Genre>> {
    return repository.getPageObservable(offset, limit)
        .flatMap {
          GenreMapper.mapToModel(it).toSingletonObservable()
        }.io()
  }
}
