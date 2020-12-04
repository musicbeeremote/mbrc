package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.sync.SyncResult
import com.kelsos.mbrc.features.library.sync.SyncWorkHandler
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.launch

class LibraryViewModel(
  private val dispatchers: AppCoroutineDispatchers,
  private val searchModel: LibrarySearchModel,
  private val syncWorkHandler: SyncWorkHandler
) : BaseViewModel<SyncResult>() {

  val syncProgress = syncWorkHandler.syncProgress()

  fun refresh() {
    viewModelScope.launch(dispatchers.network) {
      syncWorkHandler.sync()
    }
  }

  fun search(search: String = "") {
    searchModel.search(search)
  }
}
