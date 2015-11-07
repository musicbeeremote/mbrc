package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;

public class MuteChangeEvent {
  private boolean mute;

  private MuteChangeEvent(Builder builder) {
    mute = builder.mute;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(@NonNull MuteChangeEvent copy) {
    Builder builder = new Builder();
    builder.mute = copy.mute;
    return builder;
  }

  public boolean isMute() {
    return mute;
  }

  /**
   * {@code MuteChangeEvent} builder static inner class.
   */
  public static final class Builder {
    private boolean mute;

    private Builder() {
    }

    /**
     * Sets the {@code mute} and returns a reference to this Builder so that the methods can be chained together.
     *
     * @param val the {@code mute} to set
     * @return a reference to this Builder
     */
    @NonNull public Builder withMute(boolean val) {
      mute = val;
      return this;
    }

    /**
     * Returns a {@code MuteChangeEvent} built from the parameters previously set.
     *
     * @return a {@code MuteChangeEvent} built with parameters of this {@code MuteChangeEvent.Builder}
     */
    @NonNull public MuteChangeEvent build() {
      return new MuteChangeEvent(this);
    }
  }
}
