package com.kelsos.mbrc.content.library.albums

import androidx.lifecycle.LiveData
import androidx.paging.DataSource

data class AlbumsModel(
  @field:Sorting.Fields
  val sorting: Int,
  val indexes: LiveData<List<String>>,
  val factory: DataSource.Factory<Int, AlbumEntity>
)