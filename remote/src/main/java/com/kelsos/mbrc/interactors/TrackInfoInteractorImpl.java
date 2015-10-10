package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.TrackInfo;
import com.kelsos.mbrc.rest.RemoteApi;

import rx.Single;

public class TrackInfoInteractorImpl implements TrackInfoInteractor {
  @Inject private RemoteApi api;
  @Override
  public Single<TrackInfo> execute() {
    return api.getTrackInfo();
  }
}
