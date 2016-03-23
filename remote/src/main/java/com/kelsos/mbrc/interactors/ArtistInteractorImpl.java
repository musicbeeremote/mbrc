package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.mappers.ArtistMapper;
import com.kelsos.mbrc.repository.library.ArtistRepository;
import rx.Observable;

public class ArtistInteractorImpl implements ArtistInteractor {

  @Inject private ArtistRepository repository;

  @Override public Observable<Artist> getArtist(long id) {
    return Observable.defer(() -> Observable.just(ArtistMapper.map(repository.getById(id))));
  }
}
