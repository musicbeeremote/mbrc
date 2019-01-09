package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.interfaces.data.Mapper

class AlbumMapper : Mapper<AlbumEntity, AlbumInfo> {
  override fun map(from: AlbumEntity): AlbumInfo {
    return AlbumInfo(from.album, from.artist)
  }
}