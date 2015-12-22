package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.mappers.ArtistMapper;
import com.kelsos.mbrc.repository.LibraryRepository;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibraryArtistInteractor {
  @Inject private LibraryRepository repository;

  public Observable<List<Artist>> execute(int offset, int limit) {
    return repository.getArtists(offset, limit)
        .flatMap(artists -> Observable.just(ArtistMapper.mapData(artists)))
        .subscribeOn(Schedulers.io());
  }
}
