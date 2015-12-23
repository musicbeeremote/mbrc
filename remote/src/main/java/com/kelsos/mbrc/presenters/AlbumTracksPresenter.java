package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.AlbumTrackView;

public interface AlbumTracksPresenter {
  void bind(AlbumTrackView view);

  void load(long albumId);

  void play();
}
