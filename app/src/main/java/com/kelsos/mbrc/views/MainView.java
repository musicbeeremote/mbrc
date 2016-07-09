package com.kelsos.mbrc.views;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.domain.TrackInfo;
import com.kelsos.mbrc.enums.LfmStatus;
import com.kelsos.mbrc.events.ui.ShuffleChange;

public interface MainView extends BaseView {

  void updateCover(@Nullable Bitmap cover);

  void updateShuffleState(@ShuffleChange.ShuffleState String shuffleState);

  void updateRepeat(@Repeat.Mode String mode);

  void updateVolume(int volume, boolean mute);

  void updatePlayState(@PlayerState.State String state);

  void updateTrackInfo(TrackInfo info);

  void updateConnection(int status);

  void updateScrobbleStatus(boolean active);

  void updateLfmStatus(LfmStatus status);
}
