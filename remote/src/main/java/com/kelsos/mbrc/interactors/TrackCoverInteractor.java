package com.kelsos.mbrc.interactors;

import android.graphics.Bitmap;

import rx.Observable;

public interface TrackCoverInteractor {
  Observable<Bitmap> execute();
}
