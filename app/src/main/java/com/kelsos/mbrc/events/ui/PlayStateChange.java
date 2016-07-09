package com.kelsos.mbrc.events.ui;


import android.support.annotation.NonNull;

import static com.kelsos.mbrc.annotations.PlayerState.State;

public class PlayStateChange {
  @State
  private String state;

  private PlayStateChange(Builder builder) {
    state = builder.state;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(@NonNull PlayStateChange copy) {
    Builder builder = new Builder();
    builder.state = copy.state;
    return builder;
  }

  public
  @State
  String getState() {
    return this.state;
  }

  /**
   * {@code PlayStateChange} builder static inner class.
   */
  public static final class Builder {
    @State
    private String state;

    private Builder() {
    }

    /**
     * Sets the {@code state} and returns a reference to this Builder so that the methods can be chained together.
     *
     * @param val the {@code state} to set
     * @return a reference to this Builder
     */
    @NonNull
    public Builder state(@State @NonNull String val) {
      state = val;
      return this;
    }

    /**
     * Returns a {@code PlayStateChange} built from the parameters previously set.
     *
     * @return a {@code PlayStateChange} built with parameters of this {@code PlayStateChange.Builder}
     */
    @NonNull
    public PlayStateChange build() {
      return new PlayStateChange(this);
    }
  }
}
