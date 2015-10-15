package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.dto.player.PlaybackState;
import com.kelsos.mbrc.dto.player.Repeat;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.player.Volume;

import rx.Single;

public interface PlayerRepository {
  Single<Shuffle> getShuffleState();
  Single<Volume> getVolume();
  Single<PlaybackState> getPlaybackState();
  Single<Boolean> getMute();
  Single<Repeat> getRepeat();
}
