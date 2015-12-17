package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.RemoteDatabase;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.mappers.AlbumMapper;
import com.kelsos.mbrc.repository.LibraryRepository;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibraryAlbumInteractorImpl implements LibraryAlbumInteractor {
  @Inject private LibraryRepository repository;

  @Override public Observable<List<Album>> execute(int offset, int limit) {
    return repository.getAlbums(offset, limit).flatMap(albums -> {
      final List<Album> data = new ArrayList<>();
      TransactionManager.transact(RemoteDatabase.NAME, () -> {
        data.addAll(AlbumMapper.map(albums));
      });
      return Observable.just(data);
    }).subscribeOn(Schedulers.io());
  }
}
