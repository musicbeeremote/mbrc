package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Genre;
import com.kelsos.mbrc.ui.views.BrowseGenreView;

public interface BrowseGenrePresenter {
  void bind(BrowseGenreView view);

  void load();

  void queue(Genre genre, @Queue.Action String action);
}
