package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.interfaces.data.Mapper

class AlbumMapper : Mapper<Album, AlbumInfo> {
  override fun map(from: Album): AlbumInfo {
    return AlbumInfo(from.album ?: "", from.artist ?: "", from.cover)
  }
}
