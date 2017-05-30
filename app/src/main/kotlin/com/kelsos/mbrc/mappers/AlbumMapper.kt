package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.domain.AlbumInfo
import com.kelsos.mbrc.library.albums.Album

class AlbumMapper : Mapper<Album, AlbumInfo> {
  override fun map(from: Album): AlbumInfo {
    return AlbumInfo(from.album ?: "", from.artist ?: "", from.cover)
  }
}
