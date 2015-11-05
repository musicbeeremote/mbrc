package com.kelsos.mbrc.cache;

import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.dto.player.PlayState;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.player.Volume;

public interface PlayerCache {
  Shuffle getShuffle();

  void setShuffle(Shuffle shuffle);

  Volume getVolume();

  void setVolume(Volume volume);

  PlayState getPlayState();

  void setPlayState(PlayState playState);

  boolean isMute();

  void setMute(boolean mute);

  @Repeat.Mode String getRepeat();

  void setRepeat(@Repeat.Mode String repeat);
}
