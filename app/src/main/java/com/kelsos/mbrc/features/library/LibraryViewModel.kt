package com.kelsos.mbrc.features.library

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.content.sync.SyncResult
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.launch

class LibraryViewModel(
  private val dispatchers: AppCoroutineDispatchers,
  private val librarySyncUseCase: LibrarySyncUseCase
) : BaseViewModel<SyncResult>() {

  fun refresh() {
    viewModelScope.launch(dispatchers.network) {
      emit(librarySyncUseCase.sync())
    }
  }
}
