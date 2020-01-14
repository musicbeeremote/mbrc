package com.kelsos.mbrc.features.library

import androidx.lifecycle.LiveData
import androidx.paging.DataSource

data class DataModel<T>(
  val factory: DataSource.Factory<Int, T>,
  val indexes: LiveData<List<String>>
)