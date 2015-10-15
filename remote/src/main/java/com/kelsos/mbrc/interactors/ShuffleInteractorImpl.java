package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.ShuffleState;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.requests.ShuffleRequest;
import com.kelsos.mbrc.services.api.PlayerService;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShuffleInteractorImpl implements ShuffleInteractor {
  @Inject private PlayerService api;
  @Override
  public Single<Shuffle> execute() {
    return api.getShuffleState()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Single<Shuffle> execute(@ShuffleState String state) {
    return api.updateShuffleState(new ShuffleRequest().setStatus(state))
        .observeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
