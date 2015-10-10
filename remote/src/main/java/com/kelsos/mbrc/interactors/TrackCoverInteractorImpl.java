package com.kelsos.mbrc.interactors;

import android.graphics.Bitmap;

import com.google.inject.Inject;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.utilities.RemoteUtils;

import rx.Single;

public class TrackCoverInteractorImpl implements TrackCoverInteractor {
  @Inject private RemoteApi api;
  @Override
  public Single<Bitmap> getCover() {
    return api.getTrackCover(RemoteUtils.getTimeStamp());
  }
}
