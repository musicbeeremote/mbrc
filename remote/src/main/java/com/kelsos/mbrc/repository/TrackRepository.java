package com.kelsos.mbrc.repository;

import android.graphics.Bitmap;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;
import java.util.List;
import rx.Observable;

public interface TrackRepository {
  Observable<TrackInfo> getTrackInfo(boolean reload);

  Observable<List<String>> getTrackLyrics(boolean reload);

  Observable<Bitmap> getTrackCover(boolean reload);

  Observable<TrackPosition> getPosition();

  void setPosition(TrackPosition position);

  Observable<Rating> getRating();

  void setRating(Rating rating);

  void setTrackInfo(TrackInfo trackInfo);

  void setLyrics(List<String> lyrics);

  void setTrackCover(Bitmap cover);
}
