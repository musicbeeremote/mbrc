package com.kelsos.mbrc.content.playlists

import com.kelsos.mbrc.networking.protocol.Page
import io.reactivex.Observable

interface PlaylistService {
  fun fetch(offset: Int, limit: Int): Observable<Page<PlaylistDto>>
}