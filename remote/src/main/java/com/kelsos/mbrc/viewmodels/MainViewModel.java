package com.kelsos.mbrc.viewmodels;

import android.graphics.Bitmap;
import android.support.annotation.IntRange;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.dto.track.TrackInfo;

import static com.kelsos.mbrc.annotations.Shuffle.State;

public interface MainViewModel {
  @State String getShuffle();

  void setShuffle(@State String shuffle);

  @PlayerState.State String getPlayState();

  void setPlayState(@PlayerState.State String playState);

  @Repeat.Mode String getRepeat();

  void setRepeat(@Repeat.Mode String repeat);

  boolean isMuted();

  void setMuted(boolean muted);

  TrackInfo getTrackInfo();

  void setTrackInfo(TrackInfo trackInfo);

  Bitmap getTrackCover();

  void setTrackCover(Bitmap cover);

  TrackPosition getPosition();

  void setPosition(TrackPosition position);

  float getRating();

  void setRating(float rating);

  @IntRange(from = 0, to = 100) int getVolume();

  void setVolume(@IntRange(from = 0, to = 100) int volume);

  boolean isLoaded();

  void loadComplete();
}
