package com.kelsos.mbrc.content.now_playing

import com.kelsos.mbrc.networking.protocol.Page

import io.reactivex.Observable

interface NowPlayingService {
  fun getNowPlaying(offset: Int, limit: Int): Observable<Page<NowPlaying>>
}
