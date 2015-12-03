package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.requests.ChangeStateRequest;
import com.kelsos.mbrc.repository.PlayerRepository;
import com.kelsos.mbrc.services.api.PlayerService;

import rx.Observable;
import rx.schedulers.Schedulers;

public class MuteInteractorImpl implements MuteInteractor {
  @Inject private PlayerRepository repository;
  @Inject private PlayerService service;

  @Override public Observable<Boolean> execute(boolean reload) {
    return repository.getMute(reload);
  }

  @Override public Observable<Boolean> execute() {
    return repository.getMute(false)
        .subscribeOn(Schedulers.io())
        .flatMap(enabled -> service.updateMuteState(new ChangeStateRequest().setEnabled(enabled))
            .flatMap(statusResponse -> {
              repository.setMute(statusResponse.getEnabled());
              return Observable.just(statusResponse.getEnabled());
            }));

  }
}
