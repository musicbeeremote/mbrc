package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.domain.QueueTrack;

import java.util.List;

import rx.Observable;

public interface NowPlayingListInteractor {
  Observable<List<QueueTrack>> execute();
}
