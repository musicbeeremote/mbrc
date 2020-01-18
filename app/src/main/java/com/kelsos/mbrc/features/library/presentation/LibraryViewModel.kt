package com.kelsos.mbrc.features.library.presentation

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.sync.LibrarySyncUseCase
import com.kelsos.mbrc.features.library.sync.SyncResult
import com.kelsos.mbrc.ui.BaseViewModel
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
