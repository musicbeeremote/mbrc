package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.requests.ShuffleRequest;
import com.kelsos.mbrc.services.api.PlayerService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShuffleInteractorImpl implements ShuffleInteractor {
  @Inject private PlayerService api;

  @Override public Observable<String> getShuffle() {
    return api.getShuffleState()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(shuffle -> Observable.just(shuffle.getState()));
  }

  @Override public Observable<String> updateShuffle(@com.kelsos.mbrc.annotations.Shuffle.State String status) {
      ShuffleRequest request = new ShuffleRequest();
      request.setStatus(status);
      return api.updateShuffleState(request)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(shuffle -> Observable.just(shuffle.getState()));
  }
}
