package com.kelsos.mbrc.models;

import android.graphics.Bitmap;

import com.kelsos.mbrc.dto.PlaybackState;
import com.kelsos.mbrc.dto.Position;
import com.kelsos.mbrc.dto.Rating;
import com.kelsos.mbrc.dto.Repeat;
import com.kelsos.mbrc.dto.Shuffle;
import com.kelsos.mbrc.dto.TrackInfo;
import com.kelsos.mbrc.dto.Volume;

public interface MainViewModel {
  Shuffle getShuffle();

  void setShuffle(Shuffle shuffle);

  PlaybackState getPlaybackState();

  void setPlaybackState(PlaybackState playbackState);

  Repeat getRepeat();

  void setRepeat(Repeat repeat);

  boolean isMuted();

  void setMuted(boolean muted);

  TrackInfo getTrackInfo();

  void setTrackInfo(TrackInfo trackInfo);

  Bitmap getTrackCover();

  void setTrackCover(Bitmap cover);

  Position getPosition();

  void setPosition(Position position);

  Rating getRating();

  void setRating(Rating rating);

  Volume getVolume();

  void setVolume(Volume volume);
}
