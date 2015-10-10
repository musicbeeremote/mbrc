package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.annotations.PlaybackAction;
import com.kelsos.mbrc.rest.responses.SuccessResponse;

import rx.Single;

public interface PlayerInteractor {
  Single<SuccessResponse> execute(@PlaybackAction String action);
}
