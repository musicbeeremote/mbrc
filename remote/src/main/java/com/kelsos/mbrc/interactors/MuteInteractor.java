package com.kelsos.mbrc.interactors;

import rx.Observable;

public interface MuteInteractor {
  /**
   * Requests the mute state from the repository.
   *
   * @param reload Tells the repository to either bring the cached data or reload from remote.
   * @return An {@link Observable} for the repository state. The state is a boolean (either enabled or disabled)
   */
  Observable<Boolean> execute(boolean reload);

  /**
   * Requests to toggle the state of mute
   * @return An {@link Observable} for the mute state. The state is a boolean (either enabled or disabled)
   */
  Observable<Boolean> execute();
}
