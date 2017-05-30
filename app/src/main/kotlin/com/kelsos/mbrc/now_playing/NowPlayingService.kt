package com.kelsos.mbrc.now_playing

import com.kelsos.mbrc.data.Page

import io.reactivex.Observable

interface NowPlayingService {
  fun getNowPlaying(offset: Int, limit: Int): Observable<Page<NowPlaying>>
}
