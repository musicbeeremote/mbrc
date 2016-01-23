package com.kelsos.mbrc.cache;

import android.support.annotation.IntRange;
import com.kelsos.mbrc.annotations.Mute;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.annotations.Shuffle;

public interface PlayerStateCache {
  @Shuffle.State String getShuffle();

  void setShuffle(@Shuffle.State String shuffle);

  @IntRange(from = 0, to = 100) int getVolume();

  void setVolume(@IntRange(from = 0, to = 100) int volume);

  @PlayerState.State String getPlayState();

  void setPlayState(@PlayerState.State String playState);

  @Mute.State int getMuteState();

  void setMuteState(@Mute.State int mute);

  @Repeat.Mode String getRepeat();

  void setRepeat(@Repeat.Mode String repeat);
}
