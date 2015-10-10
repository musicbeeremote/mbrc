package com.kelsos.mbrc.repository;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.Lyrics;
import com.kelsos.mbrc.dto.Position;
import com.kelsos.mbrc.dto.Rating;
import com.kelsos.mbrc.dto.TrackInfo;

import rx.Observable;
import rx.Single;

public interface TrackRepository {
  Single<TrackInfo> getTrackInfo();
  Observable<Lyrics> getTrackLyrics();
  Single<Bitmap> getTrackCover();
  Observable<Position> getPosition();
  Single<Rating> getRating();
}
