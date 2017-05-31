package com.kelsos.mbrc.content.playlists

import com.kelsos.mbrc.networking.protocol.Page
import io.reactivex.Observable

interface PlaylistService {

  fun getPlaylists(offset: Int, limit: Int): Observable<Page<Playlist>>
}
