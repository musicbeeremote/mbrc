package com.kelsos.mbrc.ui.navigation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
  private val dispatchers: AppCoroutineDispatchers,
  private val librarySyncUseCase: LibrarySyncUseCase,
) : ViewModel() {

  private val _events: MutableSharedFlow<Event<Int>> = MutableSharedFlow()

  val events: MutableSharedFlow<Event<Int>>
    get() = this._events

  fun refresh() {
    viewModelScope.launch(dispatchers.network) {
      librarySyncUseCase.sync()
    }
  }
}
