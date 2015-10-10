package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.Rating;
import com.kelsos.mbrc.rest.RemoteApi;

import rx.Single;

public class TrackRatingInteractorImpl implements TrackRatingInteractor {
  @Inject private RemoteApi api;

  @Override
  public Single<Rating> execute() {
    return api.getTrackRating();
  }
}
