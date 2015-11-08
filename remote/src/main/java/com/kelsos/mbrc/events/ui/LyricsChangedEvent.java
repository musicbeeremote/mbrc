package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;

import java.util.List;

public class LyricsChangedEvent {

  private List<String> lyrics;

  private LyricsChangedEvent(Builder builder) {
    lyrics = builder.lyrics;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(@NonNull LyricsChangedEvent copy) {
    Builder builder = new Builder();
    builder.lyrics = copy.lyrics;
    return builder;
  }

  public List<String> getLyrics() {
    return this.lyrics;
  }

  /**
   * {@code LyricsChangedEvent} builder static inner class.
   */
  public static final class Builder {
    private List<String> lyrics;

    private Builder() {
    }

    /**
     * Sets the {@code lyrics} and returns a reference to this Builder so that the methods can be chained together.
     *
     * @param val the {@code lyrics} to set
     * @return a reference to this Builder
     */
    @NonNull public Builder withLyrics(@NonNull List<String> val) {
      lyrics = val;
      return this;
    }

    /**
     * Returns a {@code LyricsChangedEvent} built from the parameters previously set.
     *
     * @return a {@code LyricsChangedEvent} built with parameters of this {@code LyricsChangedEvent.Builder}
     */
    @NonNull public LyricsChangedEvent build() {
      return new LyricsChangedEvent(this);
    }
  }
}
