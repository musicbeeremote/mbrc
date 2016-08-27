package com.kelsos.mbrc.domain;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AlbumInfo implements Parcelable {
  public abstract String album();

  public abstract String artist();

  public static Builder builder() {
    return new AutoValue_AlbumInfo.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder album(String album);

    public abstract Builder artist(String artist);

    public abstract AlbumInfo build();
  }
}
