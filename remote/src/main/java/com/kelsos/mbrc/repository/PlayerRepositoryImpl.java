package com.kelsos.mbrc.repository;

import android.text.TextUtils;

import com.google.inject.Inject;
import com.kelsos.mbrc.annotations.Repeat;
import com.kelsos.mbrc.cache.PlayerCache;
import com.kelsos.mbrc.dto.player.PlaybackState;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.player.Volume;
import com.kelsos.mbrc.interactors.ShuffleInteractor;
import com.kelsos.mbrc.interactors.VolumeInteractor;
import com.kelsos.mbrc.services.api.PlayerService;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlayerRepositoryImpl implements PlayerRepository {
  @Inject private PlayerCache playerCache;
  @Inject private ShuffleInteractor shuffleInteractor;
  @Inject private VolumeInteractor volumeInteractor;
  @Inject private PlayerService service;

  @Override
  public Single<Shuffle> getShuffleState() {
    if (playerCache.getShuffle() == null) {
      return shuffleInteractor.execute()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(shuffle -> {
            playerCache.setShuffle(shuffle);
            return Single.just(shuffle);
          });
    } else {
      return Single.just(playerCache.getShuffle());
    }
  }

  @Override
  public Single<Volume> getVolume() {
    if (playerCache.getVolume() == null) {
      return volumeInteractor.execute()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread()).flatMap(volume -> {
            playerCache.setVolume(volume);
            return Single.just(volume);
          });

    } else  {
      return Single.just(playerCache.getVolume());
    }
  }

  @Override
  public Single<PlaybackState> getPlaybackState() {
    return null;
  }

  @Override
  public Single<Boolean> getMute() {
    return null;
  }

  @Override
  public Observable<String> getRepeat(boolean reload) {
    final Observable<String> remote = service.getRepeatMode()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(repeat -> {
          playerCache.setRepeat(repeat.getValue());
          return Observable.just(repeat.getValue());
        });

    return reload ? remote : Observable.concat(Observable.just(playerCache.getRepeat()), remote)
        .filter(s -> !TextUtils.isEmpty(s))
        .first();

  }

  @Override
  public void setRepeat(@Repeat.Mode String repeat) {
    playerCache.setRepeat(repeat);
  }

  @Override
  public void setVolume(Volume volume) {
    playerCache.setVolume(volume);
  }
}
