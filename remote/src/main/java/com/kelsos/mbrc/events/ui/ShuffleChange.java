package com.kelsos.mbrc.events.ui;

import com.kelsos.mbrc.annotations.ShuffleState;

public class ShuffleChange {

  private String shuffleState;

  public ShuffleChange(@ShuffleState String shuffleState) {
    this.shuffleState = shuffleState;
  }

  @ShuffleState public String getShuffleState() {
    return this.shuffleState;
  }
}
