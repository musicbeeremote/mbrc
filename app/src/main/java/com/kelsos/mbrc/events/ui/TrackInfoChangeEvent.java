package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.domain.TrackInfo;

public class TrackInfoChangeEvent {

  private final TrackInfo trackInfo;

  private TrackInfoChangeEvent(Builder builder) {
    trackInfo = builder.trackInfo;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(@NonNull TrackInfoChangeEvent copy) {
    Builder builder = new Builder();
    builder.trackInfo = copy.trackInfo;
    return builder;
  }

  public TrackInfo getTrackInfo() {
    return trackInfo;
  }

  /**
   * {@code TrackInfoChangeEvent} builder static inner class.
   */
  public static final class Builder {
    private TrackInfo trackInfo;

    private Builder() {
    }

    /**
     * Sets the {@code trackInfo} and returns a reference to this Builder so that the methods can be chained together.
     *
     * @param val the {@code trackInfo} to set
     * @return a reference to this Builder
     */
    @NonNull public Builder trackInfo(@NonNull TrackInfo val) {
      trackInfo = val;
      return this;
    }

    /**
     * Returns a {@code TrackInfoChangeEvent} built from the parameters previously set.
     *
     * @return a {@code TrackInfoChangeEvent} built with parameters of this {@code TrackInfoChangeEvent.Builder}
     */
    @NonNull public TrackInfoChangeEvent build() {
      return new TrackInfoChangeEvent(this);
    }
  }
}
