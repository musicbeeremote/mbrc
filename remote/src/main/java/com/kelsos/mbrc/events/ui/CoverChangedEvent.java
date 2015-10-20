package com.kelsos.mbrc.events.ui;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public class CoverChangedEvent {
  private Bitmap cover;

  private CoverChangedEvent(Bitmap cover) {
    this.cover = cover;
  }

  public boolean isAvailable() {
    return this.cover != null;
  }

  @Nullable
  public Bitmap getCover() {
    return this.cover;
  }

  public static CoverChangedEvent newInstance(@Nullable Bitmap cover) {
    return new CoverChangedEvent(cover);
  }
}
