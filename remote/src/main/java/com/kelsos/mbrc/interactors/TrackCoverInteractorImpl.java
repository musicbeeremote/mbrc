package com.kelsos.mbrc.interactors;

import android.graphics.Bitmap;

import com.google.inject.Inject;
import com.kelsos.mbrc.services.api.TrackService;
import com.kelsos.mbrc.utilities.RemoteUtils;

import rx.Observable;

public class TrackCoverInteractorImpl implements TrackCoverInteractor {
  @Inject private TrackService api;
  @Override
  public Observable<Bitmap> execute() {
    return api.getTrackCover(RemoteUtils.getTimeStamp());
  }
}
