package com.kelsos.mbrc.interactors.library;

import com.kelsos.mbrc.domain.Artist;
import java.util.List;
import rx.Observable;

public interface GenreArtistInteractor {
  Observable<List<Artist>> getGenreArtists();
}
