package com.kelsos.mbrc.cache;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.track.Lyrics;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;

public interface TrackCache {
  TrackInfo getTrackinfo();

  void setTrackinfo(TrackInfo trackinfo);

  Lyrics getLyrics();

  void setLyrics(Lyrics lyrics);

  Bitmap getCover();

  void setCover(Bitmap cover);

  Position getPosition();

  void setPosition(Position position);

  Rating getRating();

  void setRating(Rating rating);
}
