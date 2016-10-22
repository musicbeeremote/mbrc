package com.kelsos.mbrc.interactors

import javax.inject.Inject
import com.kelsos.mbrc.annotations.PlayerAction.Action
import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable

class PlayerInteractorImpl : PlayerInteractor {
  @Inject private lateinit var api: PlayerService

  override fun performAction(@Action action: String): Observable<BaseResponse> {
    return api.performPlayerAction(action)
  }
}
