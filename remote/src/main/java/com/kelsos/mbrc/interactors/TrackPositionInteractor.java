package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.domain.TrackPosition;

import rx.Observable;

public interface TrackPositionInteractor {
  Observable<TrackPosition> getPosition();
  Observable<TrackPosition> setPosition(int position);
}
