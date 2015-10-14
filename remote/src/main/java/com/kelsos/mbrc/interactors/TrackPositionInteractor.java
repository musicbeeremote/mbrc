package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.Position;

import rx.Single;

public interface TrackPositionInteractor {
  Single<Position> execute();
}
