package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.requests.PositionRequest;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.services.api.TrackService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TrackPositionInteractorImpl implements TrackPositionInteractor {
  @Inject private TrackService api;
  @Override
  public Observable<Position> execute() {
    return api.getCurrentPosition()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<Position> execute(int position) {
    return api.updatePosition(new PositionRequest().setPosition(position))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
