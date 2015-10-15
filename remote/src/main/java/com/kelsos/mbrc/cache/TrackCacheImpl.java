package com.kelsos.mbrc.cache;

import android.graphics.Bitmap;

import com.google.inject.Singleton;
import com.kelsos.mbrc.dto.track.Lyrics;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;

@Singleton
public class TrackCacheImpl implements TrackCache {
  private TrackInfo trackinfo;
  private Lyrics lyrics;
  private Bitmap cover;
  private Position position;
  private Rating rating;

  @Override
  public TrackInfo getTrackinfo() {
    return trackinfo;
  }

  @Override
  public void setTrackinfo(TrackInfo trackinfo) {
    this.trackinfo = trackinfo;
  }

  @Override
  public Lyrics getLyrics() {
    return lyrics;
  }

  @Override
  public void setLyrics(Lyrics lyrics) {
    this.lyrics = lyrics;
  }

  @Override
  public Bitmap getCover() {
    return cover;
  }

  @Override
  public void setCover(Bitmap cover) {
    this.cover = cover;
  }

  @Override
  public Position getPosition() {
    return position;
  }

  @Override
  public void setPosition(Position position) {
    this.position = position;
  }

  @Override
  public Rating getRating() {
    return rating;
  }

  @Override
  public void setRating(Rating rating) {
    this.rating = rating;
  }
}
