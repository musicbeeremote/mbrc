package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.domain.Artist;
import java.util.List;

public interface ArtistAlbumsView {
  void updateArtistInfo(Artist artist);

  void update(List<Album> data);

  void showLoadFailed();

}
