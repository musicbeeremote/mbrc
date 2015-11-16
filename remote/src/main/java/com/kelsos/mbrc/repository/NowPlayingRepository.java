package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.domain.QueueTrack;

import java.util.List;

import rx.Observable;

public interface NowPlayingRepository {
  Observable<List<QueueTrack>> getNowPlayingList();
}
