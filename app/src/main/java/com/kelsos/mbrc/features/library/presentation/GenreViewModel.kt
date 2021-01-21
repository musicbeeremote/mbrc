package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.features.library.data.Genre
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.ui.UiMessageBase

abstract class GenreViewModel(
  dispatchers: AppDispatchers
) : BaseViewModel<UiMessageBase>(dispatchers) {
  abstract val genres: LiveData<PagedList<Genre>>
}
