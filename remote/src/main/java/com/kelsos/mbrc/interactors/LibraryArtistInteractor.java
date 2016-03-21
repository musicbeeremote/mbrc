package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.mappers.ArtistMapper;
import com.kelsos.mbrc.repository.library.ArtistRepository;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibraryArtistInteractor {
  @Inject private ArtistRepository repository;

  public Observable<List<Artist>> execute(int offset, int limit) {
    return repository.getPageObservable(offset, limit)
        .flatMap(artists -> Observable.just(ArtistMapper.mapData(artists)))
        .subscribeOn(Schedulers.io());
  }
}
