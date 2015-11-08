package com.kelsos.mbrc.ui.views;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.kelsos.mbrc.annotations.PlayerState;

public interface MiniControlView {
  void updatePlayerState(@PlayerState.State String state);
  void updateTrack(String artist, String title);
  void updateCover(@Nullable Bitmap cover);
}
