package com.kelsos.mbrc.cache;

import android.graphics.Bitmap;
import com.google.inject.Singleton;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.dto.track.Rating;
import java.util.List;

@Singleton public class TrackCacheImpl implements TrackCache {
  private TrackInfo trackinfo;
  private List<String> lyrics;
  private Bitmap cover;
  private TrackPosition position;
  private Rating rating;

  @Override public TrackInfo getTrackinfo() {
    return trackinfo;
  }

  @Override public void setTrackinfo(TrackInfo trackinfo) {
    this.trackinfo = trackinfo;
  }

  @Override public List<String> getLyrics() {
    return lyrics;
  }

  @Override public void setLyrics(List<String> lyrics) {
    this.lyrics = lyrics;
  }

  @Override public Bitmap getCover() {
    return cover;
  }

  @Override public void setCover(Bitmap cover) {
    this.cover = cover;
  }

  @Override public TrackPosition getPosition() {
    return position;
  }

  @Override public void setPosition(TrackPosition position) {
    this.position = position;
  }

  @Override public Rating getRating() {
    return rating;
  }

  @Override public void setRating(Rating rating) {
    this.rating = rating;
  }
}
