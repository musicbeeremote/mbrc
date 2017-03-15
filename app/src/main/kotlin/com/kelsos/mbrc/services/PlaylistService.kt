package com.kelsos.mbrc.services

import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.Playlist
import io.reactivex.Observable

interface PlaylistService {

  fun getPlaylists(offset: Int, limit: Int): Observable<Page<Playlist>>
}
