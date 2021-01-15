package com.kelsos.mbrc.features.library.presentation.details.viemodels

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase

abstract class AlbumTrackViewModel(
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  abstract val tracks: LiveData<PagedList<Track>>
  abstract fun load(album: String, artist: String)
  abstract fun queue(action: Queue, item: Track)
}
