package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.services.api.TrackService;

import rx.Single;

public class TrackInfoInteractorImpl implements TrackInfoInteractor {
  @Inject private TrackService api;
  @Override
  public Single<TrackInfo> execute() {
    return api.getTrackInfo();
  }
}
