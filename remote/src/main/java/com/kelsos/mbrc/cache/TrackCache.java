package com.kelsos.mbrc.cache;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;

import java.util.List;

public interface TrackCache {
  TrackInfo getTrackinfo();

  void setTrackinfo(TrackInfo trackinfo);

  List<String> getLyrics();

  void setLyrics(List<String> lyrics);

  Bitmap getCover();

  void setCover(Bitmap cover);

  Position getPosition();

  void setPosition(Position position);

  Rating getRating();

  void setRating(Rating rating);
}
