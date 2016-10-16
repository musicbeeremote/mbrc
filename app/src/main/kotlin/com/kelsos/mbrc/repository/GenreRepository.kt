package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Genre
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single

interface GenreRepository : Repository<Genre> {

  fun getAndSaveRemote(): Single<FlowCursorList<Genre>>
}
