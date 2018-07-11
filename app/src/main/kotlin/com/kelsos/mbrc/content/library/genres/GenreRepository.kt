package com.kelsos.mbrc.content.library.genres

import com.kelsos.mbrc.content.library.DataModel
import com.kelsos.mbrc.interfaces.data.Repository

interface GenreRepository : Repository<GenreEntity> {
  suspend fun allGenres(): DataModel<GenreEntity>
}