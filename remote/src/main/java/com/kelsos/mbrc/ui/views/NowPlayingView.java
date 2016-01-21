package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.QueueTrack;
import java.util.List;

public interface NowPlayingView {
  void updatePlayingTrack(QueueTrack track);

  void updateAdapter(List<QueueTrack> data);
}
