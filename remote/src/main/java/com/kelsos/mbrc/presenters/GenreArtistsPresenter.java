package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Artist;
import com.kelsos.mbrc.ui.views.GenreArtistView;

public interface GenreArtistsPresenter {
  void bind(GenreArtistView view);

  void onPause();

  void onResume();

  void load(long genreId);

  void queue(@Queue.Action String action, Artist artist);
}
