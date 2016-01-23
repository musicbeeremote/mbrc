package com.kelsos.mbrc.interactors;

import rx.Observable;

public interface PlayerStateInteractor {
  Observable<String> getState();
}
