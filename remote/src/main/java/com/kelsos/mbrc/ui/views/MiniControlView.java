package com.kelsos.mbrc.ui.views;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import com.kelsos.mbrc.enums.PlayState;

public interface MiniControlView {
  void updatePlaystate(PlayState state);
  void updateTrack(String artist, String title);
  void updateCover(@Nullable Bitmap cover);
}
