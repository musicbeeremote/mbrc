package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.NowPlayingTrack;

import java.util.List;

import rx.Observable;

public interface NowPlayingListInteractor {
  Observable<List<NowPlayingTrack>> execute();
}
