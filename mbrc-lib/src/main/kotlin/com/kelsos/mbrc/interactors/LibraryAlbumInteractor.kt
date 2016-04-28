package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.Album
import rx.Observable

interface LibraryAlbumInteractor {
    fun execute(offset: Int, limit: Int): Observable<List<Album>>
}
