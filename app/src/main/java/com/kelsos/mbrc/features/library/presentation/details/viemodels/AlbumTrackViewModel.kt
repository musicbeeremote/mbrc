package com.kelsos.mbrc.features.library.presentation.details.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase

class AlbumTrackViewModel(
  private val repository: TrackRepository,
  private val workHandler: WorkHandler,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  private val _tracks: MediatorLiveData<PagedList<Track>> = MediatorLiveData()
  val tracks: LiveData<PagedList<Track>>
    get() = _tracks

  fun load(album: String, artist: String) {
    _tracks.addSource(repository.getAlbumTracks(album, artist).paged()) { data ->
      _tracks.value = data
    }
  }

  fun queue(action: Queue, item: Track) {
    workHandler.queue(item.id, Meta.Track, action)
  }
}
