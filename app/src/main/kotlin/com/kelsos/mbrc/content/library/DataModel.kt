package com.kelsos.mbrc.content.library

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource

data class DataModel<T>(
  val factory: DataSource.Factory<Int, T>,
  val indexes: LiveData<List<String>>
)