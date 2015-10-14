package com.kelsos.mbrc.cache;

import com.kelsos.mbrc.dto.PlaybackState;
import com.kelsos.mbrc.dto.Repeat;
import com.kelsos.mbrc.dto.Shuffle;
import com.kelsos.mbrc.dto.Volume;

public class PlayerCacheImpl implements PlayerCache {
  private Shuffle shuffle;
  private Volume volume;
  private PlaybackState playbackState;
  private boolean mute;
  private Repeat repeat;

  @Override
  public Shuffle getShuffle() {
    return shuffle;
  }

  @Override
  public void setShuffle(Shuffle shuffle) {
    this.shuffle = shuffle;
  }

  @Override
  public Volume getVolume() {
    return volume;
  }

  @Override
  public void setVolume(Volume volume) {
    this.volume = volume;
  }

  @Override
  public PlaybackState getPlaybackState() {
    return playbackState;
  }

  @Override
  public void setPlaybackState(PlaybackState playbackState) {
    this.playbackState = playbackState;
  }

  @Override
  public boolean isMute() {
    return mute;
  }

  @Override
  public void setMute(boolean mute) {
    this.mute = mute;
  }

  @Override
  public Repeat getRepeat() {
    return repeat;
  }

  @Override
  public void setRepeat(Repeat repeat) {
    this.repeat = repeat;
  }
}
