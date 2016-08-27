package com.kelsos.mbrc.mappers;

import com.kelsos.mbrc.data.library.Album;
import com.kelsos.mbrc.domain.AlbumInfo;

public class AlbumMapper implements Mapper<Album,AlbumInfo> {
  @Override
  public AlbumInfo map(Album album) {
    return AlbumInfo.builder().album(album.getAlbum()).artist(album.getArtist()).build();
  }
}
