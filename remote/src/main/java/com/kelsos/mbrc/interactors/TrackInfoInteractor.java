package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.track.TrackInfo;

import rx.Single;

public interface TrackInfoInteractor {
  Single<TrackInfo> execute();
}
