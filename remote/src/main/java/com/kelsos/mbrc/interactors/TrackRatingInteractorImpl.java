package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.services.api.TrackService;

import rx.Observable;

public class TrackRatingInteractorImpl implements TrackRatingInteractor {
  @Inject private TrackService api;

  @Override
  public Observable<Rating> execute() {
    return api.getTrackRating();
  }
}
