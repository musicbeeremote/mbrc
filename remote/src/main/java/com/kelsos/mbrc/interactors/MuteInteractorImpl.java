package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.Mute;
import com.kelsos.mbrc.cache.PlayerStateCache;
import com.kelsos.mbrc.dto.requests.ChangeStateRequest;
import com.kelsos.mbrc.services.api.PlayerService;
import rx.Observable;
import rx.schedulers.Schedulers;

public class MuteInteractorImpl implements MuteInteractor {
  @Inject private PlayerStateCache cache;
  @Inject private PlayerService service;

  @Override public Observable<Boolean> getMuteState() {
    Observable<Integer> networkRequest = service.getMuteState()
        .flatMap(statusResponse -> Observable.just(statusResponse.getEnabled() ? Mute.ON : Mute.OFF))
        .doOnNext(cache::setMuteState);

    Observable<Integer> cached = Observable.just(cache.getMuteState());

    return Observable.concat(networkRequest, cached)
        .filter(state -> state != Mute.UNDEF)
        .map(mute -> mute == Mute.ON)
        .doOnError(throwable -> Observable.just(false))
        .first();
  }

  @Override public Observable<Boolean> toggle() {
    return Observable.just(cache.getMuteState())
        .map(integer -> integer == Mute.ON)
        .flatMap(enabled -> service.updateMuteState(new ChangeStateRequest().setEnabled(!enabled))
            .subscribeOn(Schedulers.io())
            .flatMap(response -> {
              cache.setMuteState(response.getEnabled() ? Mute.ON : Mute.OFF);
              return Observable.just(response.getEnabled());
            }))
        .subscribeOn(Schedulers.io());
  }
}
