package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.data.dao.AlbumDao
import com.kelsos.mbrc.data.views.AlbumModelView
import com.kelsos.mbrc.data.views.ArtistAlbumView
import com.kelsos.mbrc.repository.Repository
import rx.Observable

interface AlbumRepository : Repository<AlbumDao> {
    fun getAlbumViewById(albumId: Int): AlbumModelView?

    fun getAlbumViews(offset: Int, limit: Int): Observable<List<AlbumModelView>>

    fun getAlbumYear(id: Long): String

    fun getAlbumsByArtist(artistId: Long): Observable<List<ArtistAlbumView>>
}
