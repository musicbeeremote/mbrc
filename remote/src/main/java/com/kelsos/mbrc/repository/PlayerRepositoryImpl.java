package com.kelsos.mbrc.repository;

import com.google.inject.Inject;
import com.kelsos.mbrc.cache.PlayerCache;
import com.kelsos.mbrc.dto.player.PlaybackState;
import com.kelsos.mbrc.dto.player.Repeat;
import com.kelsos.mbrc.dto.player.Shuffle;
import com.kelsos.mbrc.dto.player.Volume;
import com.kelsos.mbrc.interactors.RepeatInteractor;
import com.kelsos.mbrc.interactors.ShuffleInteractor;
import com.kelsos.mbrc.interactors.VolumeInteractor;

import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlayerRepositoryImpl implements PlayerRepository {
  @Inject private PlayerCache playerCache;
  @Inject private ShuffleInteractor shuffleInteractor;
  @Inject private VolumeInteractor volumeInteractor;
  @Inject private RepeatInteractor repeatInteractor;

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
  public Single<Repeat> getRepeat() {
    if (playerCache.getRepeat() == null) {
      return repeatInteractor.execute()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(repeat -> {
            playerCache.setRepeat(repeat);
            return Single.just(repeat);
          });
    } else {
      return Single.just(playerCache.getRepeat());
    }
  }

  @Override
  public void setVolume(Volume volume) {
    playerCache.setVolume(volume);
  }
}
