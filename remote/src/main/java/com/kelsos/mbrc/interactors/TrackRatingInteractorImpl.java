package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.requests.RatingRequest;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.services.api.TrackService;
import rx.Observable;

public class TrackRatingInteractorImpl implements TrackRatingInteractor {
  @Inject private TrackService service;

  @Override public Observable<Rating> getRating() {
    return service.getTrackRating();
  }

  @Override public Observable<Float> updateRating(float rating) {
    return service.updateRating(new RatingRequest().setRating(rating))
        .flatMap(baseResponse -> Observable.just(rating));
  }
}
