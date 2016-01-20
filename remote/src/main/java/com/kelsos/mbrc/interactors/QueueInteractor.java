package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.MetaDataType;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.constants.Code;
import com.kelsos.mbrc.dto.requests.NowPlayingQueueRequest;
import com.kelsos.mbrc.services.api.NowPlayingService;
import rx.Observable;
import rx.schedulers.Schedulers;

public class QueueInteractor {
  @Inject private NowPlayingService service;

  public Observable<Boolean> execute(@MetaDataType.Type String type, @Queue.Action String action, int id) {
    final NowPlayingQueueRequest body = new NowPlayingQueueRequest();
    body.setAction(action);
    body.setId(id);
    body.setType(type);
    return service.nowplayingQueue(body)
        .flatMap(baseResponse -> Observable.just(baseResponse.getCode() == Code.SUCCESS))
        .subscribeOn(Schedulers.io());
  }

  public Observable<Boolean> execute(@Queue.Action String action, String path) {
    final NowPlayingQueueRequest body = new NowPlayingQueueRequest();
    body.setAction(action);
    body.setPath(path);
    return service.nowplayingQueue(body)
        .flatMap(baseResponse -> Observable.just(baseResponse.getCode() == Code.SUCCESS))
        .subscribeOn(Schedulers.io());
  }
}
