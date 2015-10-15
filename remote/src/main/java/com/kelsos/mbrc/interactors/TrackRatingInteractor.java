package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.track.Rating;

import rx.Single;

public interface TrackRatingInteractor {
  Single<Rating> execute();
}
