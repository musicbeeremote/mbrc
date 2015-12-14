package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.annotations.Queue.Action;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.ui.views.BrowseTrackView;

public interface BrowseTrackPresenter {
  void bind(BrowseTrackView view);

  void load();

  void queue(Track track, @Action String action);
}
