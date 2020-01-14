package com.kelsos.mbrc.features.library.genres

import com.kelsos.mbrc.features.library.DataModel
import com.kelsos.mbrc.interfaces.data.Repository

interface GenreRepository : Repository<Genre> {
  fun allGenres(): DataModel<Genre>
}