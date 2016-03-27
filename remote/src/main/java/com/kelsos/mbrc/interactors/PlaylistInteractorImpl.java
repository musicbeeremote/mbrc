package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Playlist;
import com.kelsos.mbrc.repository.PlaylistRepository;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PlaylistInteractorImpl implements PlaylistInteractor {

  @Inject private PlaylistRepository repository;

  @Override public Observable<List<Playlist>> getAllPlaylists() {
    return repository.getPlaylists()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @Override public Observable<List<Playlist>> getUserPlaylists() {
    return repository.getUserPlaylists()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
