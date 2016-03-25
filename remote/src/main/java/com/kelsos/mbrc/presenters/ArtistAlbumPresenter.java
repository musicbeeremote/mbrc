package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.ui.views.ArtistAlbumsView;

public interface ArtistAlbumPresenter {
  void load(long artistId);

  void bind(ArtistAlbumsView view);

  void queue(@Queue.Action String action, Album album);
}
