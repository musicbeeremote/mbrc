package com.kelsos.mbrc.ui.navigation.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RadioViewModel(
  private val radioRepository: RadioRepository,
  private val queueApi: QueueApi,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  private val viewModelJob: Job = Job()
  private val networkScope: CoroutineScope = CoroutineScope(dispatchers.network + viewModelJob)

  val radios: LiveData<PagedList<RadioStation>> = radioRepository.getAll().paged()

  fun refresh() {
    networkScope.launch(dispatchers.network) {
      radioRepository.getRemote()
    }
  }

  fun play(path: String) {
    networkScope.launch(dispatchers.network) {
      queueApi.queue(LibraryPopup.NOW, listOf(path))
    }
  }

  override fun onCleared() {
    viewModelJob.cancel()
    super.onCleared()
  }
}