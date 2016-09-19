package com.kelsos.mbrc.services

import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.data.Page

import rx.Observable


interface NowPlayingService {
  fun getNowPlaying(offset: Int, limit: Int): Observable<Page<NowPlaying>>
}
