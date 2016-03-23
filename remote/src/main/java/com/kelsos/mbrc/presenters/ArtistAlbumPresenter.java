package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.ArtistAlbumsView;

public interface ArtistAlbumPresenter {
  void load(long artistId);

  void bind(ArtistAlbumsView view);
}
