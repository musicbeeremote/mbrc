package com.kelsos.mbrc.features.library.presentation.details.viemodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.library.repositories.AlbumRepository
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase

class ArtistAlbumViewModel(
  private val repository: AlbumRepository,
  private val workHandler: WorkHandler,
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  private val _albums: MediatorLiveData<PagedList<Album>> = MediatorLiveData()
  val albums: LiveData<PagedList<Album>>
    get() = _albums

  fun load(artist: String) {
    _albums.addSource(repository.getAlbumsByArtist(artist).paged()) { data -> _albums.value = data }
  }

  fun queue(action: Queue, item: Album) {
    workHandler.queue(item.id, Meta.Album, action)
  }
}
