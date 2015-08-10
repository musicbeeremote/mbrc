package com.kelsos.mbrc.data.model;

import android.graphics.Bitmap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kelsos.mbrc.annotations.LastfmState;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.rest.responses.TrackResponse;

@Singleton public class TrackModel {
  private float rating;
  private String title;
  private String artist;
  private String album;
  private String year;
  private String lyrics;
  private LfmStatus lfmRating;
  private Bitmap albumCover;
  private String path;

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

  public TrackModel setTrackInfo(TrackResponse response) {
    this.artist = response.getArtist();
    this.album = response.getAlbum();
    this.year = response.getYear();
    this.title = response.getTitle();
    this.path = response.getPath();
    return this;
  }

  public float getRating() {
    return rating;
  }

  public TrackModel setRating(float rating) {
    this.rating = rating;
    return this;
  }

  public String getTitle() {
    return title;
  }

  public TrackModel setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getArtist() {
    return artist;
  }

  public TrackModel setArtist(String artist) {
    this.artist = artist;
    return this;
  }

  public String getAlbum() {
    return album;
  }

  public TrackModel setAlbum(String album) {
    this.album = album;
    return this;
  }

  public String getYear() {
    return year;
  }

  public TrackModel setYear(String year) {
    this.year = year;
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

  public String getPath() {
    return path;
  }

  public TrackModel setPath(String path) {
    this.path = path;
    return this;
  }
}
