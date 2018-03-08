package com.kelsos.mbrc.content.library.artists

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource

data class Artists(
  val factory: DataSource.Factory<Int, ArtistEntity>,
  val indexes: LiveData<List<String>>
)