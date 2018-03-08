package com.kelsos.mbrc.content.library.tracks

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource

data class Tracks(
  val factory: DataSource.Factory<Int, TrackEntity>,
  val indexes: LiveData<List<String>>
)
