package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.Rating;

import rx.Observable;
import rx.Single;

public interface TrackRatingInteractor {
  Single<Rating> execute();
}
