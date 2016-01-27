package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.dto.BaseResponse;

import rx.Observable;

public interface PlayerInteractor {
  Observable<BaseResponse> performAction(@PlayerAction.Action String action);
}
