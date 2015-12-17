package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.ui.views.BrowseAlbumView;

public interface BrowseAlbumPresenter {
  void bind(BrowseAlbumView view);

  void queue(Album album, @Queue.Action String action);

  void load();

  void load(int page);
}
