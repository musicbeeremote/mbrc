package com.kelsos.mbrc.interactors;

import java.util.List;

import rx.Observable;

public interface TrackLyricsInteractor {
  Observable<List<String>> execute(boolean reload);
}
