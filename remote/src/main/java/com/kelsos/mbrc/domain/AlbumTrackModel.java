package com.kelsos.mbrc.domain;

import java.util.List;

public class AlbumTrackModel {
  private final List<Track> tracks;
  private final Album dao;

  public AlbumTrackModel(List<Track> tracks, Album dao) {

    this.tracks = tracks;
    this.dao = dao;
  }

  public List<Track> getTracks() {
    return tracks;
  }

  public Album getAlbum() {
    return dao;
  }
}
