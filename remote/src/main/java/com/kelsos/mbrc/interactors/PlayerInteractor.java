package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.annotations.PlayerAction;
import com.kelsos.mbrc.dto.BaseResponse;

import rx.Single;

public interface PlayerInteractor {
  Single<BaseResponse> execute(@PlayerAction.Action String action);
}
