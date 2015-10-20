package com.kelsos.mbrc.interactors;

import android.graphics.Bitmap;

import com.google.inject.Inject;
import com.kelsos.mbrc.services.api.TrackService;
import com.kelsos.mbrc.utilities.RemoteUtils;

import rx.Single;

public class TrackCoverInteractorImpl implements TrackCoverInteractor {
  @Inject private TrackService api;
  @Override
  public Single<Bitmap> execute() {
    return api.getTrackCover(RemoteUtils.getTimeStamp());
  }
}
