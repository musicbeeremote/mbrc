package com.kelsos.mbrc.cache;

import android.support.annotation.IntRange;

import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.annotations.Shuffle.State;

public interface PlayerCache {
  @State String getShuffle();

  void setShuffle(@State String val);

  @IntRange(from = 0, to = 100) int getVolume();

  void setVolume(@IntRange(from = 0, to = 100) int val);

  @PlayerState.State String getPlayState();

  void setPlayState(@PlayerState.State String val);

  boolean isMute();

  void setMute(boolean mute);

  @Repeat.Mode String getRepeat();

  void setRepeat(@Repeat.Mode String repeat);
}
