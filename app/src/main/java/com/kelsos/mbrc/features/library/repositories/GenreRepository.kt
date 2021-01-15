package com.kelsos.mbrc.features.library.repositories

import androidx.paging.DataSource
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.features.library.data.Genre

interface GenreRepository : Repository<Genre> {
  fun allGenres(): DataSource.Factory<Int, Genre>
}
