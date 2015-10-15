package com.kelsos.mbrc.repository;

import android.graphics.Bitmap;

import com.google.inject.Inject;
import com.kelsos.mbrc.cache.TrackCache;
import com.kelsos.mbrc.dto.track.Lyrics;
import com.kelsos.mbrc.dto.track.Position;
import com.kelsos.mbrc.dto.track.Rating;
import com.kelsos.mbrc.dto.track.TrackInfo;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackInfoInteractor;
import com.kelsos.mbrc.interactors.TrackLyricsInteractor;
import com.kelsos.mbrc.interactors.TrackPositionInteractor;
import com.kelsos.mbrc.interactors.TrackRatingInteractor;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TrackRepositoryImpl implements TrackRepository {
  @Inject private TrackInfoInteractor trackInfoUseCase;
  @Inject private TrackRatingInteractor trackRatingUserCase;
  @Inject private TrackCoverInteractor trackCoverUserCase;
  @Inject private TrackLyricsInteractor trackLyricsUseCase;
  @Inject private TrackPositionInteractor trackPositionInteractor;
  @Inject private TrackCache cache;
  @Override
  public Single<TrackInfo> getTrackInfo() {
    if (cache.getTrackinfo() == null) {
      return trackInfoUseCase.execute()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(trackInfo -> {
            cache.setTrackinfo(trackInfo);
            return Single.create(subscriber -> subscriber.onSuccess(trackInfo));
          });
    } else {
      return Single.just(cache.getTrackinfo());
    }
  }

  @Override
  public Single<Lyrics> getTrackLyrics() {
    if (cache.getLyrics() == null) {
      return trackLyricsUseCase.execute()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(lyrics -> {
            cache.setLyrics(lyrics);
            return Single.just(lyrics);
          });
    } else {
      return Single.just(cache.getLyrics());
    }
  }

  @Override
  public Single<Bitmap> getTrackCover() {
    if (cache.getCover() == null) {
      return trackCoverUserCase.getCover()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(bitmap -> {
            cache.setCover(bitmap);
            return Single.just(bitmap);
          });
    } else {
      return Single.just(cache.getCover());
    }
  }

  @Override
  public Observable<Position> getPosition() {
    if (cache.getPosition() == null) {
      return trackPositionInteractor.execute().subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(position -> {
            cache.setPosition(position);
            return Observable.just(position);
          });
    } else {
      return Observable.just(cache.getPosition());
    }
  }

  @Override
  public Single<Rating> getRating() {
    if (cache.getRating() == null) {
      return trackRatingUserCase.execute()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .flatMap(rating -> {
            cache.setRating(rating);
            return Single.just(rating);
          });
    } else {
      return Single.just(cache.getRating());
    }
  }
}
