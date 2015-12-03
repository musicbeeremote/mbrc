package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.mappers.PlaylistMapper;
import com.kelsos.mbrc.services.api.PlaylistService;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PlaylistInteractorImpl implements PlaylistInteractor {
  private static final int LIMIT = 400;
  @Inject private PlaylistService service;
  @Override public Observable<List<Playlist>> execute() {
    return Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getPlaylists(LIMIT * integer, LIMIT)
            .subscribeOn(Schedulers.io()))
        .takeWhile(page -> page.getOffset() < page.getTotal())
        .flatMap(page -> Observable.just(PlaylistMapper.map(page.getData())));
  }
}
