package com.kelsos.mbrc.features.library.repositories

import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.features.library.data.DataModel
import com.kelsos.mbrc.features.library.data.Genre

interface GenreRepository : Repository<Genre> {
  fun allGenres(): DataModel<Genre>
}