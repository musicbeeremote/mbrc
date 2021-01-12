package com.kelsos.mbrc.features.library.presentation.details.viemodels

import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler

class GenreArtistViewModelImpl(
  private val repository: ArtistRepository,
  private val workHandler: WorkHandler,
  dispatchers: AppCoroutineDispatchers
) : GenreArtistViewModel(dispatchers) {
  override val artists = MediatorLiveData<PagedList<Artist>>()

  override fun load(genre: String) {
    artists.addSource(repository.getArtistByGenre(genre).paged()) { data -> artists.value = data }
  }

  override fun queue(action: Queue, item: Artist) {
    workHandler.queue(item.id, Meta.Artist, action)
  }
}
