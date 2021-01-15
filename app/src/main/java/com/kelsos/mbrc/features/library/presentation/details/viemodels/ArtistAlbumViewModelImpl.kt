package com.kelsos.mbrc.features.library.presentation.details.viemodels

import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler

class ArtistAlbumViewModelImpl(
  private val repository: AlbumRepository,
  private val workHandler: WorkHandler,
  dispatchers: AppCoroutineDispatchers
) : ArtistAlbumViewModel(dispatchers) {
  override val albums = MediatorLiveData<PagedList<Album>>()

  override fun load(artist: String) {
    albums.addSource(repository.getAlbumsByArtist(artist).paged()) { data -> albums.value = data }
  }

  override fun queue(action: Queue, item: Album) {
    workHandler.queue(item.id, Meta.Album, action)
  }
}
