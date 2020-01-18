package com.kelsos.mbrc.features.library.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.repositories.TrackRepository

class TrackViewModel(repository: TrackRepository) : ViewModel() {

  val tracks: LiveData<PagedList<Track>>
  val indexes: LiveData<List<String>>

  init {
    val allTracks = repository.allTracks()
    tracks = allTracks.factory.paged()
    indexes = allTracks.indexes
  }
}