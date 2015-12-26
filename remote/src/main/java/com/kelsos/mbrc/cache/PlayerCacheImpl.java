package com.kelsos.mbrc.cache;

import android.support.annotation.IntRange;

import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.Repeat;

import static com.kelsos.mbrc.annotations.Shuffle.State;

public class PlayerCacheImpl implements PlayerCache {
  @State private String shuffle;
  @IntRange(from = 0, to = 100) private int volume;
  @PlayerState.State private String playState;
  private boolean mute;
  @Repeat.Mode private String repeat;

  @Override
  @State public String getShuffle() {
    return shuffle;
  }

  @Override
  public void setShuffle(@State String shuffle) {
    this.shuffle = shuffle;
  }

  @Override
  @IntRange(from = 0, to = 100) public int getVolume() {
    return volume;
  }

  @Override
  public void setVolume(@IntRange(from = 0, to = 100) int volume) {
    this.volume = volume;
  }

  @Override
  @PlayerState.State public String getPlayState() {
    return playState;
  }

  @Override
  public void setPlayState(@PlayerState.State String playState) {
    this.playState = playState;
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
  @Repeat.Mode public String getRepeat() {
    return repeat;
  }

  @Override
  public void setRepeat(@Repeat.Mode String repeat) {
    this.repeat = repeat;
  }
}
