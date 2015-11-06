package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.player.Volume;
import com.kelsos.mbrc.dto.requests.VolumeRequest;
import com.kelsos.mbrc.repository.PlayerRepository;
import com.kelsos.mbrc.services.api.PlayerService;

import rx.Observable;
import rx.Single;

public class VolumeInteractor {
  @Inject private PlayerService api;
  @Inject private PlayerRepository repository;
  public Observable<Volume> execute(boolean reload) {
    return repository.getVolume(reload);
  }
  public Single<Volume> execute(int volume) {
    return api.updateVolume(new VolumeRequest().setValue(volume));
  }
}
