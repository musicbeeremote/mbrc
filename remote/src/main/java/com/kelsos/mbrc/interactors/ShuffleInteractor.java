package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.annotations.ShuffleState;
import com.kelsos.mbrc.dto.Shuffle;

import rx.Single;

public interface ShuffleInteractor {
  Single<Shuffle> execute();
  Single<Shuffle> execute(@ShuffleState String state);
}
