package com.kelsos.mbrc.playlists

import com.kelsos.mbrc.data.Page
import io.reactivex.Observable

interface PlaylistService {

  fun getPlaylists(offset: Int, limit: Int): Observable<Page<Playlist>>
}
