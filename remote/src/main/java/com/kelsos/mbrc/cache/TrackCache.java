package com.kelsos.mbrc.cache;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.Lyrics;
import com.kelsos.mbrc.dto.Position;
import com.kelsos.mbrc.dto.Rating;
import com.kelsos.mbrc.dto.TrackInfo;

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
