package com.kelsos.mbrc.ui.views;

import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import com.kelsos.mbrc.annotations.ShuffleState;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.enums.PlayState;
import com.kelsos.mbrc.dto.TrackInfo;

public interface MainView {
  void updateCover(Bitmap bitmap);

  void updateShuffle(@ShuffleState String state);

  void updateRepeat(boolean enabled);

  void updateScrobbling(boolean enabled);

  void updateLoved(LfmStatus status);

  void updateVolume(int volume);

  void updatePlaystate(PlayState playstate);

  void updateMute(boolean enabled);

  @UiThread void updateProgress(int progress, int min, int sec);

  void updateDuration(int total, int min, int sec);

  int getCurrentProgress();

  void setStoppedState();

  void updateTrackInfo(TrackInfo trackInfo);
}
