package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.player.Shuffle;

import rx.Single;

public interface ShuffleInteractor {
  Single<Shuffle> execute();
  Single<Shuffle> execute(@com.kelsos.mbrc.annotations.Shuffle.State String state);
}
