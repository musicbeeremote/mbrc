package com.kelsos.mbrc.interactors;

import rx.Observable;

public interface MuteInteractor {
  /**
   * Requests the mute state from the repository.
   *
   * @return An {@link Observable} for the repository state. The state is a boolean (either enabled or disabled)
   */
  Observable<Boolean> getMuteState();

  /**
   * Requests to toggle the state of mute
   * @return An {@link Observable} for the mute state. The state is a boolean (either enabled or disabled)
   */
  Observable<Boolean> toggle();
}
