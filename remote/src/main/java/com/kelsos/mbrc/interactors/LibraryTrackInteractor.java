package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.domain.Track;
import java.util.List;
import rx.Observable;

public interface LibraryTrackInteractor {
  Observable<List<Track>> execute();
}
