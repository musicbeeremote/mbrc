package com.kelsos.mbrc.repository;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.track.Lyrics;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;

import rx.Observable;
import rx.Single;

public interface TrackRepository {
  Single<TrackInfo> getTrackInfo();
  Single<Lyrics> getTrackLyrics();
  Single<Bitmap> getTrackCover();
  Observable<Position> getPosition();
  Single<Rating> getRating();

  void setTrackInfo(TrackInfo trackInfo);
  void setLyrics(Lyrics lyrics);
  void setTrackCover(Bitmap cover);
  void setPosition(Position position);
  void setRating(Rating rating);
}
