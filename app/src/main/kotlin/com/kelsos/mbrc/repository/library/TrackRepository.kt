package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.data.dao.TrackDao
import com.kelsos.mbrc.data.views.TrackModelView
import com.kelsos.mbrc.repository.Repository
import rx.Observable

interface TrackRepository : Repository<TrackDao> {
    fun getTracksByAlbumId(albumId: Long): Observable<List<TrackModelView>>
    fun getTracks(offset: Int, limit: Int): Observable<List<TrackModelView>>
}
