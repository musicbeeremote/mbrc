package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.QueueTrack;
import java.util.List;

public interface NowPlayingView {
  void updatePlayingTrack(QueueTrack track);
  void removeTrack(int position);
  void moveTrack(int from, int to);
  void updateAdapter(List<QueueTrack> data);
}
