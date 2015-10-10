package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dto.PlaybackState;
import com.kelsos.mbrc.dto.Shuffle;
import com.kelsos.mbrc.dto.Volume;

import rx.Single;

public interface PlayerRepository {
  Single<Shuffle> getShuffleState();
  Single<Volume> getVolume();
  Single<PlaybackState> getRepeat();
  Single<Boolean> getMute();
}
