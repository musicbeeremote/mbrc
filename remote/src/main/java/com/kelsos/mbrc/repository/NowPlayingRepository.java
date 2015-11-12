package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dto.NowPlayingTrack;

import java.util.List;

import rx.Observable;

public interface NowPlayingRepository {
  Observable<List<NowPlayingTrack>> getNowPlayingList();
}
