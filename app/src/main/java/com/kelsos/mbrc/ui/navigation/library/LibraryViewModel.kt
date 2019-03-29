package com.kelsos.mbrc.ui.navigation.library

import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.content.sync.SyncResult
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
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