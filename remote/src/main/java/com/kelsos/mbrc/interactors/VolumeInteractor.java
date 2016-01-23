package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.player.Volume;
import com.kelsos.mbrc.dto.requests.VolumeRequest;
import com.kelsos.mbrc.services.api.PlayerService;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VolumeInteractor {
  @Inject private PlayerService api;

  public Observable<Integer> getVolume() {
    return api.getVolume().map(Volume::getValue).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
  }

  public Observable<Integer> setVolume(int volume) {
    return api.updateVolume(new VolumeRequest().setValue(volume))
        .map(Volume::getValue)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
