package com.kelsos.mbrc.repository;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;

import java.util.List;

import rx.Observable;

public interface TrackRepository {
  Observable<TrackInfo> getTrackInfo(boolean reload);
  Observable<List<String>> getTrackLyrics(boolean reload);
  Observable<Bitmap> getTrackCover(boolean reload);
  Observable<Position> getPosition();
  Observable<Rating> getRating();

  void setTrackInfo(TrackInfo trackInfo);
  void setLyrics(List<String> lyrics);
  void setTrackCover(Bitmap cover);
  void setPosition(Position position);
  void setRating(Rating rating);
}
