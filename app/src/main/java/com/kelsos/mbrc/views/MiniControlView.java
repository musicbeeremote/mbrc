package com.kelsos.mbrc.views;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.kelsos.mbrc.annotations.PlayerState.State;
import com.kelsos.mbrc.domain.TrackInfo;

public interface MiniControlView extends BaseView {
  void updateCover(@Nullable Bitmap cover);

  void updateTrackInfo(TrackInfo trackInfo);

  void updateState(@State String state);
}
