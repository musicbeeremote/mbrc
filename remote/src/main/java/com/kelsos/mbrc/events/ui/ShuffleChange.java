package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.annotations.Shuffle;

public class ShuffleChange {

  private String shuffleState;

  public ShuffleChange(@Shuffle.State String shuffleState) {
    this.shuffleState = shuffleState;
  }

  @Shuffle.State public String getShuffleState() {
    return this.shuffleState;
  }
}
