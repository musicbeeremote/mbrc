package com.kelsos.mbrc.features.library.presentation.details.viemodels

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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapMerge

class GenreArtistViewModel(
  private val repository: ArtistRepository,
  private val workHandler: WorkHandler
) : BaseViewModel<UiMessageBase>() {
  private val genreFlow: MutableSharedFlow<String> = MutableSharedFlow()

  @OptIn(FlowPreview::class)
  val artists: Flow<PagingData<Artist>> = genreFlow.flatMapMerge {
    repository.getArtistByGenre(it)
  }.cachedIn(viewModelScope)

  fun load(genre: String) {
    genreFlow.tryEmit(genre)
  }

  fun queue(action: Queue, item: Artist) {
    workHandler.queue(item.id, Meta.Artist, action)
  }
}
