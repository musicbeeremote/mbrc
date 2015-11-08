package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.track.TrackInfo;

import rx.Observable;

public interface TrackInfoInteractor {
  Observable<TrackInfo> execute(boolean reload);
}
