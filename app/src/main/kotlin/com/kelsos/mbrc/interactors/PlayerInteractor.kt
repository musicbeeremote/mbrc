package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.annotations.PlayerAction
import com.kelsos.mbrc.dto.BaseResponse

import rx.Observable

interface PlayerInteractor {
    fun performAction(@PlayerAction.Action action: String): Observable<BaseResponse>
}
