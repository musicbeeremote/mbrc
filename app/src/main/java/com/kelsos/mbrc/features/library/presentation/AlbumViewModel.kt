package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.features.library.data.Album
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase

abstract class AlbumViewModel(
  dispatchers: AppDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  abstract val albums: LiveData<PagedList<Album>>
}
