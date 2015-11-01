package com.kelsos.mbrc.cache;

import com.kelsos.mbrc.annotations.RepeatMode;
import com.kelsos.mbrc.dto.player.PlaybackState;
import com.kelsos.mbrc.dto.player.Repeat;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.player.Volume;

public class PlayerCacheImpl implements PlayerCache {
  private Shuffle shuffle;
  private Volume volume;
  private PlaybackState playbackState;
  private boolean mute;
  @RepeatMode private String repeat;

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
  @RepeatMode public String getRepeat() {
    return repeat;
  }

  @Override
  public void setRepeat(@RepeatMode String repeat) {
    this.repeat = repeat;
  }
}
