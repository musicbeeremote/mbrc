package com.kelsos.mbrc.features.radio.presentation

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RadioViewModel(
  private val radioRepository: RadioRepository,
  private val queue: QueueHandler,
  private val dispatchers: AppCoroutineDispatchers
) : BaseViewModel<RadioRefreshResult>() {

  val radios: Flow<PagingData<RadioStation>> = radioRepository.getAll().cachedIn(viewModelScope)

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      emit(
        radioRepository.getRemote()
          .toEither()
          .fold(
            {
              RadioRefreshResult.RefreshFailed
            },
            {
              RadioRefreshResult.RefreshSuccess
            }
          )
      )
    }
  }

  fun play(path: String) {
    viewModelScope.launch(dispatchers.network) {
      queue.queuePath(path)
    }
  }
}
