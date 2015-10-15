package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.dto.BaseResponse;
import com.kelsos.mbrc.services.api.PlayerService;

import rx.Single;

public class PlayerInteractorImpl implements PlayerInteractor {
  @Inject private PlayerService api;
  @Override
  public Single<BaseResponse> execute(@PlaybackAction String action) {
    return api.performPlayerAction(action);
  }
}
