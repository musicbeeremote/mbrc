package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.GenreArtistView;

public interface GenreArtistsPresenter {
  void bind(GenreArtistView view);

  void onPause();

  void onResume();

  void load(long genreId);
}
