package com.kelsos.mbrc.cache;

import android.graphics.Bitmap;
import com.kelsos.mbrc.domain.TrackPosition;
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

  TrackPosition getPosition();

  void setPosition(TrackPosition position);

  Rating getRating();

  void setRating(Rating rating);
}
