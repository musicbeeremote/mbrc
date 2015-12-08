package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.PlaylistTrackView;

public interface PlaylistTrackPresenter {
  void bind(PlaylistTrackView view);

  void load();
}
