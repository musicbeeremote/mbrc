package com.kelsos.mbrc.services;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kelsos.mbrc.constants.Protocol;
import com.kelsos.mbrc.data.NowPlaying;
import com.kelsos.mbrc.data.Page;
import com.kelsos.mbrc.data.PageRange;
import com.kelsos.mbrc.data.SocketMessage;

import java.io.IOException;

import rx.Observable;

public class NowPlayingServiceImpl extends ServiceBase implements NowPlayingService {
  @Override
  public Observable<Page<NowPlaying>> getNowPlaying(int offset, int limit) {
    PageRange range = getPageRange(offset, limit);
    return request(Protocol.NowPlayingList,  range == null ? "" : range).flatMap(this::getPageObservable);
  }

  @NonNull
  private Observable<Page<NowPlaying>> getPageObservable(SocketMessage socketMessage) {
    return Observable.create(subscriber -> {
      try {
        TypeReference<Page<NowPlaying>> typeReference = new TypeReference<Page<NowPlaying>>() {
        };
        Page<NowPlaying> page = mapper.readValue((String) socketMessage.getData(), typeReference);
        subscriber.onNext(page);
        subscriber.onCompleted();
      } catch (IOException e) {
        subscriber.onError(e);
      }
    });
  }
}
