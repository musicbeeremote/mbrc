package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.TrackPosition;
import com.kelsos.mbrc.dto.requests.PositionRequest;
import com.kelsos.mbrc.services.api.TrackService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TrackPositionInteractorImpl implements TrackPositionInteractor {
  @Inject private TrackService service;

  @Override public Observable<TrackPosition> getPosition() {
    return service.getCurrentPosition()
        .subscribeOn(Schedulers.io())
        .flatMap(position -> Observable.just(new TrackPosition(position.getPosition(), position.getDuration())))
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<TrackPosition> setPosition(int position) {
    return service.updatePosition(new PositionRequest().setPosition(position))
        .subscribeOn(Schedulers.io())
        .flatMap(updated -> Observable.just(new TrackPosition(updated.getPosition(), updated.getDuration())))
        .observeOn(AndroidSchedulers.mainThread());
  }
}
