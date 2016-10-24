package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.dao.AlbumDao
import com.kelsos.mbrc.data.dao.ArtistDao
import com.kelsos.mbrc.data.dao.CoverDao
import com.kelsos.mbrc.data.dao.GenreDao

object MapperUtils {

  internal fun getCoverById(id: Long, data: List<CoverDao>): CoverDao? {
    return data.filter { it.id == id }.firstOrNull()
  }

  internal fun getArtistById(id: Long, data: List<ArtistDao>): ArtistDao? {
    return data.filter { it.id == id }.firstOrNull()
  }

  internal fun getAlbumById(id: Long, data: List<AlbumDao>): AlbumDao? {
    return data.filter { it.id == id }.firstOrNull()
  }

  internal fun getGenreById(id: Long, data: List<GenreDao>): GenreDao? {
    return data.filter { it.id == id }.firstOrNull()
  }
}
