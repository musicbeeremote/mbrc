package com.kelsos.mbrc.cache;

import com.kelsos.mbrc.dto.player.PlaybackState;
import com.kelsos.mbrc.dto.player.Repeat;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.player.Volume;

public interface PlayerCache {
  Shuffle getShuffle();

  void setShuffle(Shuffle shuffle);

  Volume getVolume();

  void setVolume(Volume volume);

  PlaybackState getPlaybackState();

  void setPlaybackState(PlaybackState playbackState);

  boolean isMute();

  void setMute(boolean mute);

  Repeat getRepeat();

  void setRepeat(Repeat repeat);
}
