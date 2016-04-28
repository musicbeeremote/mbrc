package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.constants.Code;
import com.kelsos.mbrc.dto.requests.PlayPathRequest;
import com.kelsos.mbrc.services.api.PlaylistService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlaylistActionInteractor {
  @Inject private PlaylistService service;

  public Observable<Boolean> play(String path) {
      PlayPathRequest request = new PlayPathRequest();
      request.setPath(path);
      return service.playPlaylist(request)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(baseResponse -> Observable.just(baseResponse.getCode() == Code.SUCCESS));
  }
}
