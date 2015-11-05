package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.player.PlayState;
import com.kelsos.mbrc.repository.PlayerRepository;

import rx.Observable;

public class PlayerStateInteractorImpl implements PlayerStateInteractor {
  @Inject private PlayerRepository repository;
  @Override public Observable<PlayState> execute(boolean reload) {
    return repository.getPlayState(reload);
  }
}
