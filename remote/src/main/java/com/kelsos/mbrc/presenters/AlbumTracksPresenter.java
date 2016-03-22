package com.kelsos.mbrc.presenters;

import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.ui.views.AlbumTrackView;

public interface AlbumTracksPresenter {
  void bind(AlbumTrackView view);

  void load(long albumId);

  void play(long albumId);

  void queue(Track entry, @Queue.Action String action);
}
