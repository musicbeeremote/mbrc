package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.PlaylistTrack;
import java.util.List;

public interface PlaylistTrackView {

  void showErrorWhileLoading();

  void update(List<PlaylistTrack> data);
}
