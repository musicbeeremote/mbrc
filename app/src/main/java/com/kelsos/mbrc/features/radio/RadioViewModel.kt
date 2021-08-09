package com.kelsos.mbrc.features.radio

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.queue.QueueUseCase
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RadioViewModel(
  private val radioRepository: RadioRepository,
  private val queueUseCase: QueueUseCase,
  private val dispatchers: AppCoroutineDispatchers
) : BaseViewModel<RadioUiMessages>() {

  val radios: Flow<PagingData<RadioStation>> = radioRepository.getAll().cachedIn(viewModelScope)

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      val result = radioRepository.getRemote()
        .fold(
          {
            RadioUiMessages.RefreshFailed
          },
          {
            RadioUiMessages.RefreshSuccess
          }
        )
      emit(result)
    }
  }

  fun play(path: String) {
    viewModelScope.launch(dispatchers.network) {
      val response = queueUseCase.queuePath(path)
      val uiMessage = if (response.success) {
        RadioUiMessages.QueueSuccess
      } else {
        RadioUiMessages.QueueFailed
      }
      emit(uiMessage)
    }
  }
}
