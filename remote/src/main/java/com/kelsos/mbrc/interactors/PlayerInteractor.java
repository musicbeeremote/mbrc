package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.dto.BaseResponse;

import rx.Single;

public interface PlayerInteractor {
  Single<BaseResponse> execute(@PlaybackAction String action);
}
