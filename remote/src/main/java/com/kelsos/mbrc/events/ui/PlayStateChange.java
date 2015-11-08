package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;

import com.kelsos.mbrc.dto.player.PlayState;

public class PlayStateChange {
  private PlayState state;

  private PlayStateChange(Builder builder) {
    state = builder.state;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(@NonNull PlayStateChange copy) {
    Builder builder = new Builder();
    builder.state = copy.state;
    return builder;
  }

  public PlayState getState() {
    return this.state;
  }

  /**
   * {@code PlayStateChange} builder static inner class.
   */
  public static final class Builder {
    private PlayState state;

    private Builder() {
    }

    /**
     * Sets the {@code state} and returns a reference to this Builder so that the methods can be chained together.
     *
     * @param val the {@code state} to set
     * @return a reference to this Builder
     */
    @NonNull public Builder withState(@NonNull PlayState val) {
      state = val;
      return this;
    }

    /**
     * Returns a {@code PlayStateChange} built from the parameters previously set.
     *
     * @return a {@code PlayStateChange} built with parameters of this {@code PlayStateChange.Builder}
     */
    @NonNull public PlayStateChange build() {
      return new PlayStateChange(this);
    }
  }
}
