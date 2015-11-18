package com.kelsos.mbrc.interactors;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.QueueTrack;
import com.kelsos.mbrc.repository.NowPlayingRepository;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NowPlayingListInteractorImpl implements NowPlayingListInteractor {
  @Inject private NowPlayingRepository repository;

  @Override public Observable<List<QueueTrack>> execute() {
    return repository.getNowPlayingList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }
}
