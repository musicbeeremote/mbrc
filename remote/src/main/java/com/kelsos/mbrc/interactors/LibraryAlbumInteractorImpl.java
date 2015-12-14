package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.mappers.AlbumMapper;
import com.kelsos.mbrc.repository.LibraryRepository;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibraryAlbumInteractorImpl implements LibraryAlbumInteractor {
  @Inject private LibraryRepository repository;
  @Override public Observable<List<Album>> execute() {
    return repository.getAlbums().flatMap(albums -> Observable.just(AlbumMapper.map(albums)))
        .subscribeOn(Schedulers.io());
  }
}
