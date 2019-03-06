package com.kelsos.mbrc.ui.navigation.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RadioViewModel(
  private val radioRepository: RadioRepository,
  private val queue: QueueHandler,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {
  val radios: Flow<PagingData<RadioStation>> = radioRepository.getAll().cachedIn(viewModelScope)

  fun refresh() {
    viewModelScope.launch(dispatchers.network) {
      radioRepository.getRemote()
    }
  }

  fun play(path: String) {
    viewModelScope.launch(dispatchers.network) {
      queue.queuePath(path)
    }
  }
}
