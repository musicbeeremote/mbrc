package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.PlaylistListView;

public interface PlaylistPresenter {
  void bind(PlaylistListView view);

  void load();

  void play(String path);
}
