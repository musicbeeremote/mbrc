package com.kelsos.mbrc.interactors.library;

import com.google.inject.Inject;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.mappers.ArtistMapper;
import com.kelsos.mbrc.repository.library.ArtistRepository;
import java.util.List;
import rx.Observable;

public class GenreArtistInteractorImpl implements GenreArtistInteractor {
  @Inject private ArtistRepository repository;

  @Override public Observable<List<Artist>> getGenreArtists(long genreId) {
    return repository.getArtistsByGenreId(genreId)
        .flatMap(genreArtistViews -> Observable.just(ArtistMapper.mapGenreArtists(genreArtistViews)));
  }
}
