package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.constants.Constants
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.mappers.ArtistMapper
import com.kelsos.mbrc.repository.library.ArtistRepository
import rx.Observable
import rx.functions.Func1
import rx.schedulers.Schedulers
import javax.inject.Inject

class LibraryArtistInteractor
@Inject constructor(private val repository: ArtistRepository){

    fun execute(offset: Int = 0, limit: Int = Constants.PAGE_SIZE): Observable<List<Artist>> {
        return repository.getPageObservable(offset, limit)
                .flatMap<List<Artist>>(Func1{ Observable.just(ArtistMapper.mapData(it)) })
                .subscribeOn(Schedulers.io())
    }
}
