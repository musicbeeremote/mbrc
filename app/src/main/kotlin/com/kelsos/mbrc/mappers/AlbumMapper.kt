package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.domain.AlbumInfo

class AlbumMapper : Mapper<Album, AlbumInfo> {
  override fun map(album: Album): AlbumInfo {
    return AlbumInfo.builder().album(album.album).artist(album.artist).build()
  }
}
