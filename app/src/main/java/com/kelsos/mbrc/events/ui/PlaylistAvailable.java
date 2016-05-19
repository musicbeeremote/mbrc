package com.kelsos.mbrc.events.ui;

import android.support.annotation.NonNull;
import com.kelsos.mbrc.data.Playlist;
import java.util.List;

public class PlaylistAvailable {
  private List<Playlist> playlist;

  private PlaylistAvailable(List<Playlist> playlist) {
    //no instance
    this.playlist = playlist;
  }

  @NonNull public static PlaylistAvailable create(List<Playlist> playlist) {
    return new PlaylistAvailable(playlist);
  }

  public List<Playlist> getPlaylist() {
    return playlist;
  }
}
