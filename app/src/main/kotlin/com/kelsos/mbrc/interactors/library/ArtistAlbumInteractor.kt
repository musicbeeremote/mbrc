package com.kelsos.mbrc.interactors.library

import com.kelsos.mbrc.domain.Album
import rx.Observable

interface ArtistAlbumInteractor {
    fun getArtistAlbums(artistId: Long): Observable<List<Album>>
}
