package com.kelsos.mbrc.interactors

import rx.Observable

interface MuteInteractor {
  /**
   * Requests the mute state from the repository.

   * @return An [Observable] for the repository state. The state is a boolean (either enabled or disabled)
   */
  fun getMuteState(): Observable<Boolean>

  /**
   * Requests to toggle the state of mute
   * @return An [Observable] for the mute state. The state is a boolean (either enabled or disabled)
   */
  fun toggle(): Observable<Boolean>
}
