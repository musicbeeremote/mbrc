package com.kelsos.mbrc.repository;

import android.graphics.Bitmap;

import com.google.inject.Inject;
import com.kelsos.mbrc.dto.Lyrics;
import com.kelsos.mbrc.dto.Position;
import com.kelsos.mbrc.dto.Rating;
import com.kelsos.mbrc.dto.TrackInfo;
import com.kelsos.mbrc.interactors.TrackCoverInteractor;
import com.kelsos.mbrc.interactors.TrackInfoInteractor;
import com.kelsos.mbrc.interactors.TrackRatingInteractor;

import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TrackRepositoryImpl implements TrackRepository {
  @Inject private TrackInfoInteractor trackInfoUseCase;
  @Inject private TrackRatingInteractor trackRatingUserCase;
  @Inject private TrackCoverInteractor trackCoverUserCase;
  @Override
  public Single<TrackInfo> getTrackInfo() {
    return trackInfoUseCase.execute().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Observable<Lyrics> getTrackLyrics() {
    return null;
  }

  @Override
  public Single<Bitmap> getTrackCover() {
    return trackCoverUserCase.getCover()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override
  public Observable<Position> getPosition() {
    return null;
  }

  @Override
  public Single<Rating> getRating() {
    return trackRatingUserCase.execute()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());

  }
}
