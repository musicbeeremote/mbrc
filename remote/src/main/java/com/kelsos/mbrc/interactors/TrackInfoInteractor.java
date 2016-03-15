package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.domain.TrackInfo;
import rx.Observable;

public interface TrackInfoInteractor {
  Observable<TrackInfo> execute(boolean reload);
}
