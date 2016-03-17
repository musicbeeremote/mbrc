package com.kelsos.mbrc.presenters;

import com.google.inject.Inject;
import com.kelsos.mbrc.interactors.library.GenreArtistInteractor;
import com.kelsos.mbrc.ui.views.GenreArtistView;
import timber.log.Timber;

public class GenreArtistsPresenterImpl implements GenreArtistsPresenter {
  @Inject private GenreArtistInteractor genreArtistInteractor;
  private GenreArtistView view;

  @Override public void bind(GenreArtistView view) {
    this.view = view;
  }

  @Override public void onPause() {

  }

  @Override public void onResume() {

  }

  @Override public void load(long genreId) {
    genreArtistInteractor.getGenreArtists(genreId).subscribe(artists -> {
      view.update(artists);
    }, t -> {
      Timber.v(t, "Failed");
    });
  }
}
