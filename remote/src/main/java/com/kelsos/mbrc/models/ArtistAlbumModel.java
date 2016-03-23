package com.kelsos.mbrc.models;

import com.kelsos.mbrc.domain.Album;
import com.kelsos.mbrc.domain.Artist;
import java.util.List;

public class ArtistAlbumModel {
  private List<Album> albums;
  private Artist artist;

  public ArtistAlbumModel(List<Album> albums, Artist artist) {
    this.albums = albums;
    this.artist = artist;
  }

  public Artist getArtist() {
    return artist;
  }

  public List<Album> getAlbums() {
    return albums;
  }
}
