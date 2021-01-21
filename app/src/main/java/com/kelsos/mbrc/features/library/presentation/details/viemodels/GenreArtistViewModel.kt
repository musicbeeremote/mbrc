package com.kelsos.mbrc.features.library.presentation.details.viemodels

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.features.library.data.Artist
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase

abstract class GenreArtistViewModel(
  dispatchers: AppDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  abstract val artists: LiveData<PagedList<Artist>>
  abstract fun load(genre: String)
  abstract fun queue(action: Queue, item: Artist)
}
