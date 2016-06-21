package com.kelsos.mbrc.services;

import com.kelsos.mbrc.data.NowPlaying;
import com.kelsos.mbrc.data.Page;

import rx.Observable;


public interface NowPlayingService {
  Observable<Page<NowPlaying>> getNowPlaying(int offset, int limit);
}
