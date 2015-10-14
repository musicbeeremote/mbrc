package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.Repeat;
import com.kelsos.mbrc.services.api.PlayerService;

import rx.Single;

public class RepeatInteractorImpl implements RepeatInteractor {
  @Inject private PlayerService api;

  @Override
  public Single<Repeat> execute() {
    return api.getRepeatMode();
  }
}
