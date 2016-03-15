package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.mappers.PlaylistMapper;
import com.kelsos.mbrc.repository.PlaylistRepository;
import com.kelsos.mbrc.services.api.PlaylistService;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PlaylistInteractorImpl implements PlaylistInteractor {
  private static final int LIMIT = 400;
  @Inject private PlaylistService service;
  @Inject private PlaylistRepository repository;

  @Override public Observable<List<Playlist>> execute() {

    Observable.range(0, Integer.MAX_VALUE - 1)
        .concatMap(integer -> service.getPlaylists(LIMIT * integer, LIMIT, 0)
            .subscribeOn(Schedulers.io()))
        .takeWhile(page -> page.getOffset() < page.getTotal()).subscribe(response -> {
      repository.savePlaylists(PlaylistMapper.mapDto(response.getData()));
    }, t -> {
      Timber.e(t, "Error retrieving a playlist page");
    }, () -> {
      Timber.v("Complete");
    });

    return repository.getPlaylists();
  }
}
