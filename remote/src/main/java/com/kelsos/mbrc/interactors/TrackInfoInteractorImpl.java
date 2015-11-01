package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.services.api.TrackService;

import rx.Observable;

public class TrackInfoInteractorImpl implements TrackInfoInteractor {
  @Inject private TrackService api;
  @Override
  public Observable<TrackInfo> execute() {
    return api.getTrackInfo();
  }
}
