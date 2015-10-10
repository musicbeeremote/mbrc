package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dto.PlaybackState;
import com.kelsos.mbrc.dto.Shuffle;
import com.kelsos.mbrc.dto.Volume;

import rx.Single;

public class PlayerRepositoryImpl implements PlayerRepository {
  @Override
  public Single<Shuffle> getShuffleState() {
    return null;
  }

  @Override
  public Single<Volume> getVolume() {
    return null;
  }

  @Override
  public Single<PlaybackState> getRepeat() {
    return null;
  }

  @Override
  public Single<Boolean> getMute() {
    return null;
  }
}
