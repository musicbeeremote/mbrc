package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.networking.protocol.Page

import io.reactivex.Observable

interface NowPlayingService {
  fun getNowPlaying(offset: Int, limit: Int): Observable<Page<NowPlayingDto>>
}