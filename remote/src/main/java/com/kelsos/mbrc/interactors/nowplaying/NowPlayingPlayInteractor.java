package com.kelsos.mbrc.interactors.nowplaying;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.requests.PlayPathRequest;
import com.kelsos.mbrc.services.api.NowPlayingService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NowPlayingPlayInteractor {

  @Inject private NowPlayingService service;

  public Observable<Boolean> execute(String path) {
    return service.nowPlayingPlayTrack(new PlayPathRequest().setPath(path))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(baseResponse -> Observable.just(baseResponse.getCode() == 200));
  }
}
