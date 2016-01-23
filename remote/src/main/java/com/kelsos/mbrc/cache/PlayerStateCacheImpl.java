package com.kelsos.mbrc.cache;

import android.support.annotation.IntRange;
import com.kelsos.mbrc.annotations.Mute;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.annotations.Shuffle;

import static com.kelsos.mbrc.annotations.Shuffle.State;

public class PlayerStateCacheImpl implements PlayerStateCache {

  @State private String shuffle;
  @IntRange(from = -1, to = 100) private int volume;
  @PlayerState.State private String playState;
  @Mute.State private int mute;
  @Repeat.Mode private String repeat;

  public PlayerStateCacheImpl() {
    shuffle = Shuffle.UNDEF;
    volume = -1;
    playState = PlayerState.UNDEFINED;
    mute = Mute.UNDEF;
    repeat = Repeat.UNDEFINED;
  }

  @Override @State public String getShuffle() {
    return shuffle;
  }

  @Override public void setShuffle(@State String shuffle) {
    this.shuffle = shuffle;
  }

  @Override @IntRange(from = -1, to = 100) public int getVolume() {
    return volume;
  }

  @Override public void setVolume(@IntRange(from = -1, to = 100) int volume) {
    this.volume = volume;
  }

  @Override @PlayerState.State public String getPlayState() {
    return playState;
  }

  @Override public void setPlayState(@PlayerState.State String playState) {
    this.playState = playState;
  }

  @Override @Mute.State public int getMuteState() {
    return mute;
  }

  @Override public void setMuteState(@Mute.State int mute) {
    this.mute = mute;
  }

  @Override @Repeat.Mode public String getRepeat() {
    return repeat;
  }

  @Override public void setRepeat(@Repeat.Mode String repeat) {
    this.repeat = repeat;
  }
}
