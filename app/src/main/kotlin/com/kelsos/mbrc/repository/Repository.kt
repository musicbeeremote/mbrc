package com.kelsos.mbrc.repository

import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.structure.Model
import rx.Single

interface Repository<T : Model> {
  fun getAllCursor(): Single<FlowCursorList<T>>
}
