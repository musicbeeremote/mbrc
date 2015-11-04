package com.kelsos.mbrc.interactors;

import android.graphics.Bitmap;

import com.google.inject.Inject;
import com.kelsos.mbrc.repository.TrackRepository;

import rx.Observable;

public class TrackCoverInteractorImpl implements TrackCoverInteractor {
  @Inject private TrackRepository repository;
  @Override
  public Observable<Bitmap> execute(boolean reload) {
    return repository.getTrackCover(reload);
  }
}
