package com.kelsos.mbrc.features.radio.presentation

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import arrow.core.Try
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RadioViewModel(
  private val radioRepository: RadioRepository,
  private val queue: QueueHandler,
  private val dispatchers: AppCoroutineDispatchers
) : BaseViewModel<RadioUiMessages>() {

  val radios: Flow<PagingData<RadioStation>> = radioRepository.getAll().cachedIn(viewModelScope)

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      val result = radioRepository.getRemote()
        .toEither()
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
      val response = Try { queue.queuePath(path) }
        .toEither()
        .fold(
          {
            RadioUiMessages.NetworkError
          },
          { response ->
            if (response.success) {
              RadioUiMessages.QueueSuccess
            } else {
              RadioUiMessages.QueueFailed
            }
          }
        )

      emit(response)
    }
  }
}
