package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.track.Rating;

import rx.Observable;

public interface TrackRatingInteractor {
  Observable<Rating> getRating();
  Observable<Float> updateRating(float rating);
}
