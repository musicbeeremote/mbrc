package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.rest.RemoteApi;
import com.kelsos.mbrc.rest.responses.SuccessResponse;

import rx.Single;

public class PlayerInteractorImpl implements PlayerInteractor {
  @Inject private RemoteApi api;
  @Override
  public Single<SuccessResponse> execute(@PlaybackAction String action) {
    return api.performPlayerAction(action);
  }
}
