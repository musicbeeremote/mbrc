package com.kelsos.mbrc.models;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.Position;
import com.kelsos.mbrc.dto.Rating;
import com.kelsos.mbrc.dto.TrackInfo;

public interface MainViewModel {
  TrackInfo getTrackInfo();
  Bitmap getTrackCover();
  Position getPosition();
  Rating getRating();

  void setTrackInfo(TrackInfo trackInfo);
  void setTrackCover(Bitmap cover);
  void setPosition(Position position);
  void setRating(Rating rating);
}
