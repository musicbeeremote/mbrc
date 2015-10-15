package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.player.Volume;
import com.kelsos.mbrc.dto.requests.VolumeRequest;
import com.kelsos.mbrc.services.api.PlayerService;

import rx.Single;

public class VolumeInteractor {
  @Inject private PlayerService api;
  public Single<Volume> execute() {
    return api.getVolume();
  }
  public Single<Volume> execute(int volume) {
    return api.updateVolume(new VolumeRequest().setValue(volume));
  }
}
