package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.dto.track.Lyrics;

import rx.Single;

public interface TrackLyricsInteractor {
  Single<Lyrics> execute();
}
