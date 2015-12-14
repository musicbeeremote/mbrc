package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.mappers.TrackMapper;
import com.kelsos.mbrc.repository.LibraryRepository;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class LibraryTrackInteractorImpl implements LibraryTrackInteractor {
  @Inject private LibraryRepository repository;
  @Override public Observable<List<Track>> execute() {
    return repository.getTracks()
        .subscribeOn(Schedulers.io())
        .flatMap(trackDaos -> Observable.just(TrackMapper.map(trackDaos)));
  }
}
