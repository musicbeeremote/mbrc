package com.kelsos.mbrc.features.library.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.common.Meta
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.library.repositories.ArtistRepository
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.features.work.WorkHandler
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase
import kotlinx.coroutines.flow.Flow

class GenreArtistViewModel(
  savedStateHandle: SavedStateHandle,
  repository: ArtistRepository,
  private val workHandler: WorkHandler,
) : BaseViewModel<UiMessageBase>() {
  private val genreId: Long =
    checkNotNull(
      savedStateHandle[GenreArtistDestination.GENRE_ID_ARG],
    )

  val artists: Flow<PagingData<Artist>> =
    repository.getArtistByGenre(genreId).cachedIn(
      viewModelScope,
    )

  fun queue(
    action: Queue,
    item: Artist,
  ) {
    workHandler.queue(item.id, Meta.Artist, action)
  }
}
