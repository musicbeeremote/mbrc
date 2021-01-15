package com.kelsos.mbrc.features.library.presentation.details.viemodels

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.features.queue.Queue
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase

abstract class ArtistAlbumViewModel(
  dispatchers: AppCoroutineDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  abstract val albums: LiveData<PagedList<Album>>
  abstract fun load(artist: String)
  abstract fun queue(action: Queue, item: Album)
}
