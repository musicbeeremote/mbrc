package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.Track;
import java.util.List;

public interface BrowseTrackView {

  void clearData();

  void appendPage(List<Track> tracks);
}
