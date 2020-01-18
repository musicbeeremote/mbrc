package com.kelsos.mbrc.features.library.presentation

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.sync.LibrarySyncUseCase
import com.kelsos.mbrc.features.library.sync.SyncProgressProvider
import com.kelsos.mbrc.features.library.sync.SyncResult
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.launch

class LibraryViewModel(
  dispatchers: AppCoroutineDispatchers,
  val syncProgress: SyncProgressProvider,
  private val librarySyncUseCase: LibrarySyncUseCase
) : BaseViewModel<SyncResult>(dispatchers) {

  fun refresh() {
    scope.launch {
      emit(librarySyncUseCase.sync())
    }
  }
}