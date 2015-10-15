package com.kelsos.mbrc.data.model;

import android.graphics.Bitmap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.annotations.LastfmState;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.dto.track.TrackInfo;

@Singleton public class TrackModel {
  private double rating;
  private String lyrics;
  private LfmStatus lfmRating;
  private Bitmap albumCover;
  private TrackInfo info;

  @Inject public TrackModel() {
    lyrics = "";
    rating = 0;
    lfmRating = LfmStatus.NORMAL;
  }

  public TrackModel setLfmRating(@LastfmState String rating) {
    switch (rating) {
      case LastfmState.LOVE:
        lfmRating = LfmStatus.LOVED;
        break;
      case LastfmState.BAN:
        lfmRating = LfmStatus.BANNED;
        break;
      default:
        lfmRating = LfmStatus.NORMAL;
        break;
    }
    return this;
  }

  public TrackModel setTrackInfo(TrackInfo info) {
    this.info = info;
    return this;
  }

  public double getRating() {
    return rating;
  }

  public TrackModel setRating(double rating) {
    this.rating = rating;
    return this;
  }

  public String getLyrics() {
    return lyrics;
  }

  public TrackModel setLyrics(String lyrics) {
    if (lyrics == null || lyrics.equals(this.lyrics)) {
      return this;
    }

    this.lyrics = lyrics.replace("<p>", "\r\n")
        .replace("<br>", "\n")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&amp;", "&")
        .replace("<p>", "\r\n")
        .replace("<br>", "\n")
        .trim();

    return this;
  }

  public LfmStatus getLfmRating() {
    return lfmRating;
  }

  public TrackModel setLfmRating(LfmStatus lfmRating) {
    this.lfmRating = lfmRating;
    return this;
  }

  public Bitmap getAlbumCover() {
    return albumCover;
  }

  public TrackModel setAlbumCover(Bitmap albumCover) {
    this.albumCover = albumCover;
    return this;
  }

  public TrackInfo getInfo() {
    return info;
  }
}
