package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dto.NowPlayingTrack;

import rx.Observable;

public interface NowPlayingRepository {
  Observable<NowPlayingTrack> getNowPlayingList();
}
