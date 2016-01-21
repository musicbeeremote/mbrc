package com.kelsos.mbrc.interactors.nowplaying;

import rx.Observable;

public interface NowPlayingActionInteractor {
  Observable<Boolean> play(String path);

  Observable<Boolean> remove(int position);

  Observable<Boolean> move(int from, int to);
}
