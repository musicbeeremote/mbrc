package com.kelsos.mbrc.data.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.annotations.Playstate;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.events.ui.ShuffleChange;

@Singleton public class PlayerModel {
  public static final int MAX_VOLUME = 100;
  public static final int LOWEST_NOT_ZERO = 10;
  public static final int GREATEST_NOT_MAX = 90;
  public static final int STEP = 10;
  public static final int MOD = 10;
  public static final int BASE = 0;
  public static final int LIMIT = 5;
  public static final String ALL = "All";

  boolean repeatActive;
  boolean isScrobblingActive;
  boolean isMuteActive;
  private int volume;
  private PlayState playState;
  private String shuffleState;

  @Inject public PlayerModel() {

  }

  public PlayState getPlayState() {
    return playState;
  }

  public PlayerModel setPlayState(PlayState playState) {
    this.playState = playState;
    return this;
  }

  public PlayerModel setPlayState(@Playstate String playState) {

    PlayState newState = PlayState.UNDEFINED;
    if (Playstate.PLAYING.equalsIgnoreCase(playState)) {
      newState = PlayState.PLAYING;
    } else if (Playstate.STOPPED.equalsIgnoreCase(playState)) {
      newState = PlayState.STOPPED;
    } else if (Playstate.PAUSED.equalsIgnoreCase(playState)) {
      newState = PlayState.PAUSED;
    }

    this.playState = newState;
    return this;
  }

  public int getVolume() {
    return volume;
  }

  public PlayerModel setVolume(int volume) {
    this.volume = volume;
    return this;
  }

  public boolean isMuteActive() {
    return isMuteActive;
  }

  public PlayerModel setIsMuteActive(boolean isMuteActive) {
    this.isMuteActive = isMuteActive;
    return this;
  }

  public boolean isScrobblingActive() {
    return isScrobblingActive;
  }

  public PlayerModel setIsScrobblingActive(boolean isScrobblingActive) {
    this.isScrobblingActive = isScrobblingActive;
    return this;
  }

  public boolean isRepeatActive() {
    return repeatActive;
  }

  public PlayerModel setRepeatActive(boolean repeatActive) {
    this.repeatActive = repeatActive;
    return this;
  }

  private int reduceVolume() {
    int newVolume = volume;
    if (volume >= LOWEST_NOT_ZERO) {
      int mod = volume % MOD;

      if (mod == BASE) {
        newVolume = volume - STEP;
      } else if (mod < LIMIT) {
        newVolume = volume - (STEP + mod);
      } else {
        newVolume = volume - mod;
      }
    }
    return newVolume;
  }

  private int increaseVolume() {
    int newVolume = volume;

    if (volume <= GREATEST_NOT_MAX) {
      int mod = volume % MOD;
      if (mod == BASE) {
        newVolume = volume + STEP;
      } else if (mod < LIMIT) {
        newVolume = volume + (STEP - mod);
      } else {
        newVolume = volume + ((2 * STEP) - mod);
      }
    }
    return newVolume;
  }

  @ShuffleChange.ShuffleState
  public String getShuffleState() {
    return shuffleState;
  }

  public PlayerModel setShuffleState(@ShuffleChange.ShuffleState String shuffleState) {
    this.shuffleState = shuffleState;
    return this;
  }
}
