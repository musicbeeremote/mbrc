
package com.kelsos.mbrc.events.ui;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CoverChangedEvent {
  private Bitmap cover;

  private CoverChangedEvent(Builder builder) {
    cover = builder.cover;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(@NonNull CoverChangedEvent copy) {
    Builder builder = new Builder();
    builder.cover = copy.cover;
    return builder;
  }

  public boolean isAvailable() {
    return this.cover != null;
  }

  @Nullable
  public Bitmap getCover() {
    return this.cover;
  }

  /**
   * {@code CoverChangedEvent} builder static inner class.
   */
  public static final class Builder {
    private Bitmap cover;

    private Builder() {
    }

    /**
     * Sets the {@code cover} and returns a reference to this Builder so that the methods can be chained together.
     *
     * @param val the {@code cover} to set
     * @return a reference to this Builder
     */
    @NonNull
    public Builder withCover(@NonNull Bitmap val) {
      cover = val;
      return this;
    }

    /**
     * Returns a {@code CoverChangedEvent} built from the parameters previously set.
     *
     * @return a {@code CoverChangedEvent} built with parameters of this {@code CoverChangedEvent.Builder}
     */
    @NonNull
    public CoverChangedEvent build() {
      return new CoverChangedEvent(this);
    }
  }
}
