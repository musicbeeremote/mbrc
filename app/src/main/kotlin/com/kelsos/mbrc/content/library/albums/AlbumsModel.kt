package com.kelsos.mbrc.content.library.albums

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource

data class AlbumsModel(
  @field:Sorting.Fields
  val sorting: Int,
  val indexes: LiveData<List<String>>,
  val factory: DataSource.Factory<Int, AlbumEntity>
)