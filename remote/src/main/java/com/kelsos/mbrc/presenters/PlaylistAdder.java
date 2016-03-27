package com.kelsos.mbrc.presenters;

public interface PlaylistAdder {
  void createPlaylist(long selectionId, String name);

  void playlistAdd(long selectionId, long playlistId);
}
