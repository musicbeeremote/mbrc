package com.kelsos.mbrc.ui.views;

import android.graphics.Bitmap;

import com.kelsos.mbrc.annotations.ShuffleState;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.enums.PlayState;

public interface MainView {
  void updateCover(Bitmap bitmap);

  void updateShuffle(@ShuffleState String state);

  void updateRepeat(boolean enabled);

  void updateScrobbling(boolean enabled);

  void updateLoved(LfmStatus status);

  void updateVolume(int volume);

  void updatePlaystate(PlayState playstate);

  void updateMute(boolean enabled);

  void updatePosition(TrackPosition position);

  int getCurrentProgress();

  void setStoppedState();

  void updateTrackInfo(TrackInfo trackInfo);
}
