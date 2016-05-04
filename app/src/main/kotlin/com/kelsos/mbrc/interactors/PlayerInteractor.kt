package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.annotations.PlayerAction.Action
import com.kelsos.mbrc.dto.BaseResponse
import rx.Observable

interface PlayerInteractor {
    fun performAction(@Action action: String): Observable<BaseResponse>
}
