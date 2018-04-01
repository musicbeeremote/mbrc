package com.kelsos.mbrc.content.library.genres

import com.kelsos.mbrc.content.library.DataModel
import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface GenreRepository : Repository<GenreEntity> {
  fun allGenres(): Single<DataModel<GenreEntity>>
}