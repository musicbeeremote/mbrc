package com.kelsos.mbrc.interactors;

import rx.Observable;

public interface ShuffleInteractor {
  Observable<String> getShuffle();
  Observable<String> updateShuffle(@com.kelsos.mbrc.annotations.Shuffle.State String state);
}
