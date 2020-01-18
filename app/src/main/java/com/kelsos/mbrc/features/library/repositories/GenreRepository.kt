package com.kelsos.mbrc.features.library.repositories

import com.kelsos.mbrc.features.library.data.DataModel
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.interfaces.data.Repository

interface GenreRepository : Repository<Genre> {
  fun allGenres(): DataModel<Genre>
}