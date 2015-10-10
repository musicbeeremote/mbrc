package com.kelsos.mbrc.interactors;

import android.graphics.Bitmap;

import rx.Observable;
import rx.Single;

public interface TrackCoverInteractor {
  Single<Bitmap> getCover();
}
