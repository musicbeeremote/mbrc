package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.repository.PlayerRepository;
import com.kelsos.mbrc.services.api.PlayerService;

import rx.Observable;

public class RepeatInteractorImpl implements RepeatInteractor {
  @Inject private PlayerRepository repository;
  @Inject private PlayerService service;

  @Override
  public Observable<String> execute(boolean reload) {
    return repository.getRepeat(reload);
  }

  @Override public Observable<String> execute(@Repeat.Mode String mode) {
    //Todo fix it on the plugin side to return the proper data or change the api if fixed.
    return Observable.empty();
  }
}
