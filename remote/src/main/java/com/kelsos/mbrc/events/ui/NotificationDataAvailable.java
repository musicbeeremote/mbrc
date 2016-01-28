package com.kelsos.mbrc.events.ui;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.kelsos.mbrc.annotations.PlayerState;

public class NotificationDataAvailable {
  private String artist;
  private String title;
  private String album;
  private Bitmap cover;
  @PlayerState.State private String state;

  private NotificationDataAvailable(Builder builder) {
    artist = builder.artist;
    title = builder.title;
    album = builder.album;
    cover = builder.cover;
    state = builder.state;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(@NonNull NotificationDataAvailable copy) {
    Builder builder = new Builder();
    builder.artist = copy.artist;
    builder.title = copy.title;
    builder.album = copy.album;
    builder.cover = copy.cover;
    builder.state = copy.state;
    return builder;
  }

  public String getArtist() {
    return artist;
  }

  public String getTitle() {
    return title;
  }

  public Bitmap getCover() {
    return cover;
  }

  @PlayerState.State public String getState() {
    return state;
  }

  public String getAlbum() {
    return album;
  }

  public static final class Builder {
    private String artist;
    private String title;
    private String album;
    private Bitmap cover;
    private String state;

    private Builder() {
    }

    @NonNull public Builder withArtist(@NonNull String val) {
      artist = val;
      return this;
    }

    @NonNull public Builder withTitle(@NonNull String val) {
      title = val;
      return this;
    }

    @NonNull public Builder withAlbum(@NonNull String val) {
      album = val;
      return this;
    }

    @NonNull public Builder withCover(@NonNull Bitmap val) {
      cover = val;
      return this;
    }

    @NonNull public Builder withState(@PlayerState.State @NonNull String val) {
      state = val;
      return this;
    }

    @NonNull public NotificationDataAvailable build() {
      return new NotificationDataAvailable(this);
    }
  }
}
