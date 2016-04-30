package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.dao.TrackDao
import com.kelsos.mbrc.dao.views.TrackModelView
import com.kelsos.mbrc.repository.Repository
import rx.Observable

interface TrackRepository : Repository<TrackDao> {
    fun getTracksByAlbumId(albumId: Long): Observable<List<TrackModelView>>
    fun getTracks(offset: Int, limit: Int): Observable<List<TrackModelView>>
}
