package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.mappers.AlbumMapper;
import com.kelsos.mbrc.repository.library.AlbumRepository;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibraryAlbumInteractorImpl implements LibraryAlbumInteractor {
  @Inject private AlbumRepository repository;

  @Override public Observable<List<Album>> execute(int offset, int limit) {
    return repository.getAlbumViews(offset, limit)
        .flatMap(albums -> Observable.just(AlbumMapper.map(albums)))
        .subscribeOn(Schedulers.io());
  }
}
