package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.Repeat.Mode;
import com.kelsos.mbrc.dto.requests.RepeatRequest;
import com.kelsos.mbrc.repository.PlayerRepository;
import com.kelsos.mbrc.services.api.PlayerService;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RepeatInteractorImpl implements RepeatInteractor {
  @Inject private PlayerRepository repository;
  @Inject private PlayerService service;

  @Override
  public Observable<String> execute(boolean reload) {
    return repository.getRepeat(reload);
  }

  @Override public Observable<String> execute(@Mode String mode) {
    return service.updateRepeatState(new RepeatRequest().setMode(mode))
        .subscribeOn(Schedulers.io())
        .flatMap(repeatResponse -> Observable.just(repeatResponse.getValue()));
  }
}
