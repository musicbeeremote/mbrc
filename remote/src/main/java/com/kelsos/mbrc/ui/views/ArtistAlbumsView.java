package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.Album;
import java.util.List;

public interface ArtistAlbumsView {

  void update(List<Album> data);

  void showLoadFailed();

  void queueSuccess();

  void queueFailed();
}
