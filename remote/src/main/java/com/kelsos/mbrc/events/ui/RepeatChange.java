package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;

import com.kelsos.mbrc.annotations.Repeat;

public class RepeatChange {
  @Repeat.Mode private String mode;

  public RepeatChange(@Repeat.Mode String mode) {
    this.mode = mode;
  }

  private RepeatChange(Builder builder) {
    mode = builder.mode;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(@NonNull RepeatChange copy) {
    Builder builder = new Builder();
    builder.mode = copy.mode;
    return builder;
  }

  @Repeat.Mode public String getMode() {
    return this.mode;
  }

  /**
   * {@code RepeatChange} builder static inner class.
   */
  public static final class Builder {
    private String mode;

    private Builder() {
    }

    /**
     * Sets the {@code mode} and returns a reference to this Builder so that the methods can be chained together.
     *
     * @param val the {@code mode} to set
     * @return a reference to this Builder
     */
    @NonNull public Builder withMode(@NonNull @Repeat.Mode String val) {
      mode = val;
      return this;
    }

    /**
     * Returns a {@code RepeatChange} built from the parameters previously set.
     *
     * @return a {@code RepeatChange} built with parameters of this {@code RepeatChange.Builder}
     */
    @NonNull public RepeatChange build() {
      return new RepeatChange(this);
    }
  }
}
