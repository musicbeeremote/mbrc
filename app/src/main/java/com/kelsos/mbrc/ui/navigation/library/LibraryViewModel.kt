package com.kelsos.mbrc.ui.navigation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kelsos.mbrc.content.sync.LibrarySyncUseCase
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LibraryViewModel(
  dispatchers: AppCoroutineDispatchers,
  val syncProgress: SyncProgressProvider,
  private val librarySyncUseCase: LibrarySyncUseCase
) : ViewModel() {

  private val _events: MutableLiveData<Event<Int>> = MutableLiveData()
  private val job: Job = Job()
  private val scope = CoroutineScope(dispatchers.network + job)

  val events: LiveData<Event<Int>>
    get() = this._events

  fun refresh() {
    scope.launch {
      _events.postValue(Event(librarySyncUseCase.sync()))
    }
  }

  override fun onCleared() {
    job.cancel()
    super.onCleared()
  }
}