package com.kelsos.mbrc.interactors;

import android.text.TextUtils;
import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.PlayerState;
import com.kelsos.mbrc.cache.PlayerStateCache;
import com.kelsos.mbrc.dto.player.PlayState;
import com.kelsos.mbrc.services.api.PlayerService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlayerStateInteractorImpl implements PlayerStateInteractor {
  @Inject private PlayerStateCache cache;
  @Inject private PlayerService service;

  @Override public Observable<String> getState() {
    Observable<String> networkRequest = service.getPlayState().map(PlayState::getValue).doOnNext(cache::setPlayState);
    Observable<String> cached = Observable.just(cache.getPlayState());

    return Observable.concat(networkRequest, cached)
        .filter(s -> !TextUtils.isEmpty(s) && !PlayerState.UNDEFINED.equals(s))
        .doOnError(throwable -> Observable.just(PlayerState.STOPPED))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
