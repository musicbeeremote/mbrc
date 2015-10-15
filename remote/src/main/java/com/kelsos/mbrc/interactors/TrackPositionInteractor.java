package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.track.Position;

import rx.Observable;

public interface TrackPositionInteractor {
  Observable<Position> execute();
}
