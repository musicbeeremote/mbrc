package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.Album;
import java.util.List;

public interface BrowseAlbumView {
  void updateData(List<Album> data);

  void clearData();
}
