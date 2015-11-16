package com.kelsos.mbrc.repository;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.QueueTrack;
import com.kelsos.mbrc.mappers.QueueTrackMapper;
import com.kelsos.mbrc.services.api.NowPlayingService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

public class NowPlayingRepositoryImpl implements NowPlayingRepository {

  public static final int LIMIT = 400;
  @Inject private NowPlayingService service;

  @Override
  public Observable<List<QueueTrack>> getNowPlayingList() {
    return Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getNowPlayingList(LIMIT * integer, LIMIT).subscribeOn(Schedulers.io()))
        .takeWhile(page -> page.getOffset() < page.getTotal())
        .collect(ArrayList::new, (list, page) -> list.addAll(QueueTrackMapper.map(page.getData())));
  }
}
