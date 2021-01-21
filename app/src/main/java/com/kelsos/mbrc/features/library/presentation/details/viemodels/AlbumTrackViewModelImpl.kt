package com.kelsos.mbrc.features.library.presentation.details.viemodels

import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.library.repositories.TrackRepository
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler

class AlbumTrackViewModelImpl(
  private val repository: TrackRepository,
  private val workHandler: WorkHandler,
  dispatchers: AppDispatchers
) : AlbumTrackViewModel(dispatchers) {
  override val tracks = MediatorLiveData<PagedList<Track>>()

  override fun load(album: String, artist: String) {
    tracks.addSource(repository.getAlbumTracks(album, artist).paged()) { data ->
      tracks.value = data
    }
  }

  override fun queue(action: Queue, item: Track) {
    workHandler.queue(item.id, Meta.Track, action)
  }
}
