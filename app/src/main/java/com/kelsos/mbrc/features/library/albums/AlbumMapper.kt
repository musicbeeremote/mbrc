package com.kelsos.mbrc.features.library.albums

import com.kelsos.mbrc.interfaces.data.Mapper

class AlbumMapper : Mapper<AlbumEntity, AlbumInfo> {
  override fun map(from: AlbumEntity): AlbumInfo {
    return AlbumInfo(from.album, from.artist)
  }
}