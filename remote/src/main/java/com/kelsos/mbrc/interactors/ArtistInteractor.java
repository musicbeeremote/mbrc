package com.kelsos.mbrc.interactors;

import com.kelsos.mbrc.domain.Artist;
import rx.Observable;

public interface ArtistInteractor {
  Observable<Artist> getArtist(long id);
}
