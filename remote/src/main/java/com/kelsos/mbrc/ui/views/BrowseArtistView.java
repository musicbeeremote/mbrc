package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.Artist;
import java.util.List;

public interface BrowseArtistView {
  void showEnqueueSuccess();

  void showEnqueueFailure();

  void update(List<Artist> artists);
}
