package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase

abstract class TrackViewModel(
  dispatchers: AppDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  abstract val tracks: LiveData<PagedList<Track>>
}
