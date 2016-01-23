package com.kelsos.mbrc.interactors;

import android.text.TextUtils;
import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.Repeat.Mode;
import com.kelsos.mbrc.annotations.Shuffle;
import com.kelsos.mbrc.cache.PlayerStateCache;
import com.kelsos.mbrc.dto.RepeatResponse;
import com.kelsos.mbrc.dto.player.Repeat;
import com.kelsos.mbrc.dto.requests.RepeatRequest;
import com.kelsos.mbrc.services.api.PlayerService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepeatInteractorImpl implements RepeatInteractor {
  @Inject private PlayerStateCache cache;
  @Inject private PlayerService service;

  @Override public Observable<String> getRepeat() {
    Observable<String> networkRequest = service.getRepeatMode().map(Repeat::getValue).doOnNext(cache::setRepeat);
    Observable<String> cached = Observable.just(cache.getRepeat());

    return Observable.concat(cached, networkRequest)
        .filter(state -> !TextUtils.isEmpty(state) && !Shuffle.UNDEF.equals(state))
        .doOnError(throwable -> Observable.just(Shuffle.OFF))
        .first()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<String> setRepeat(@Mode String mode) {
    return service.updateRepeatState(new RepeatRequest().setMode(mode))
        .map(RepeatResponse::getValue)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
