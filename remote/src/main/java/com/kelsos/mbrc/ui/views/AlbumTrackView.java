package com.kelsos.mbrc.ui.views;

import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.domain.Track;
import java.util.List;

public interface AlbumTrackView {
  void updateAlbum(Album album);

  void updateTracks(List<Track> tracks);
}
