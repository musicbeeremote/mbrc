package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.annotations.PlayerAction.Action
import com.kelsos.mbrc.dto.BaseResponse
import com.kelsos.mbrc.services.api.PlayerService
import rx.Observable
import javax.inject.Inject

class PlayerInteractorImpl
@Inject constructor(private val api: PlayerService) : PlayerInteractor {
  override fun performAction(@Action action: String): Observable<BaseResponse> {
    return api.performPlayerAction(action)
  }
}
