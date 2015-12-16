package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.mappers.TrackMapper;
import com.kelsos.mbrc.repository.LibraryRepository;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibraryTrackInteractorImpl implements LibraryTrackInteractor {
  public static final int PAGE_SIZE = 100;
  @Inject private LibraryRepository repository;
  @Override public Observable<List<Track>> execute(int page, int items) {
    return repository.getTracks(page * PAGE_SIZE, PAGE_SIZE)
        .subscribeOn(Schedulers.io())
        .flatMap(trackDaos -> Observable.just(TrackMapper.map(trackDaos)));
  }
}
