package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.repository.TrackRepository;

import java.util.List;

import rx.Observable;

public class TrackLyricsInteractorImpl implements TrackLyricsInteractor {

  @Inject private TrackRepository repository;

  @Override
  public Observable<List<String>> execute(boolean reload) {
    return repository.getTrackLyrics(reload);
  }
}
