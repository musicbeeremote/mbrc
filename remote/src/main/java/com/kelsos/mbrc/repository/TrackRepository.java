package com.kelsos.mbrc.repository;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.track.Lyrics;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;

import rx.Single;

public interface TrackRepository {
  Single<TrackInfo> getTrackInfo();
  Single<Lyrics> getTrackLyrics();
  Single<Bitmap> getTrackCover();
  Single<Position> getPosition();
  Single<Rating> getRating();
}
