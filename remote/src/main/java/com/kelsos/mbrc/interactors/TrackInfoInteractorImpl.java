package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.repository.TrackRepository;
import rx.Observable;

public class TrackInfoInteractorImpl implements TrackInfoInteractor {
  @Inject private TrackRepository repository;
  @Override
  public Observable<TrackInfo> execute(boolean reload) {
    return repository.getTrackInfo(reload);
  }
}
