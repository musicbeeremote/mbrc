package com.kelsos.mbrc.interactors.library;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Genre;
import com.kelsos.mbrc.mappers.GenreMapper;
import com.kelsos.mbrc.repository.library.GenreRepository;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibraryGenreInteractor {
  @Inject private GenreRepository repository;

  public Observable<List<Genre>> execute(int offset, int limit) {
    return repository.getPageObservable(offset, limit)
        .flatMap(genres -> Observable.just(GenreMapper.mapToModel(genres)))
        .subscribeOn(Schedulers.io());
  }
}
