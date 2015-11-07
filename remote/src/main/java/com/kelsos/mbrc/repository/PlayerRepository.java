package com.kelsos.mbrc.repository;

import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.dto.player.PlayState;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.player.Volume;

import rx.Observable;
import rx.Single;

public interface PlayerRepository {
  Single<Shuffle> getShuffleState();
  Observable<Volume> getVolume(boolean reload);
  Observable<PlayState> getPlayState(boolean reload);
  Observable<Boolean> getMute(boolean reload);
  Observable<String> getRepeat(boolean reload);

  void setVolume(Volume volume);
  void setRepeat(@Repeat.Mode String repeat);
  void setMute(Boolean enabled);
}
