package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.TrackInfo;

import rx.Observable;
import rx.Single;

public interface TrackInfoInteractor {
  Single<TrackInfo> execute();
}
