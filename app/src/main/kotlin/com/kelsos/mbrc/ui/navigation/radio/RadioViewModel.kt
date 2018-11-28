package com.kelsos.mbrc.ui.navigation.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStationEntity
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RadioViewModel(
  private val radioRepository: RadioRepository,
  private val queueApi: QueueApi,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel(), CoroutineScope {

  private val _radios: MediatorLiveData<PagedList<RadioStationEntity>> = MediatorLiveData()
  override val coroutineContext: CoroutineContext = Job() + dispatchers.database
  private var job: Job? = null

  init {
    launch {
      _radios.addSource(radioRepository.getAll().paged()) {
        _radios.value = it
      }
    }
  }

  val radios: LiveData<PagedList<RadioStationEntity>>
    get() = _radios

  fun refresh() {
    job = GlobalScope.launch(dispatchers.network) {
      radioRepository.getRemote()
    }
  }

  fun play(path: String) {
    job = GlobalScope.launch(dispatchers.network) {
      queueApi.queue(LibraryPopup.NOW, listOf(path))
    }
  }

  override fun onCleared() {
    job?.cancel()
    super.onCleared()
  }
}