package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.constants.Constants
import com.kelsos.mbrc.domain.Album
import rx.Observable

interface LibraryAlbumInteractor {
  fun getPage(offset: Int = 0, limit: Int = Constants.PAGE_SIZE): Observable<List<Album>>
}
