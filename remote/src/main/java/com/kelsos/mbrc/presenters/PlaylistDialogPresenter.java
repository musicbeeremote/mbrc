package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.ui.views.PlaylistDialogView;

public interface PlaylistDialogPresenter {
  void load();

  void bind(final PlaylistDialogView view);
}
