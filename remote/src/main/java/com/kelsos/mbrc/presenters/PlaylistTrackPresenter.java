package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.PlaylistTrack;
import com.kelsos.mbrc.ui.views.PlaylistTrackView;

public interface PlaylistTrackPresenter {
  void bind(PlaylistTrackView view);

  void load(long longExtra);

  void queue(PlaylistTrack track, @Queue.Action String action);
}
