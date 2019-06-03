package com.kelsos.mbrc.features.radio.presentation

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import arrow.core.Try
import com.kelsos.mbrc.features.queue.LibraryPopup
import com.kelsos.mbrc.features.queue.QueueApi
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await

class RadioViewModel(
  private val radioRepository: RadioRepository,
  private val queueApi: QueueApi,
  private val dispatchers: AppCoroutineDispatchers
) : BaseViewModel<RadioUiMessages>(dispatchers) {

  val radios: LiveData<PagedList<RadioStation>> = radioRepository.getAll().paged()

  fun reload() {
    scope.launch(dispatchers.network) {
      val result = radioRepository.getRemote()
        .toEither()
        .fold({
          RadioUiMessages.RefreshFailed
        }, {
          RadioUiMessages.RefreshSuccess
        })
      emit(result)
    }
  }

  fun play(path: String) {
    scope.launch(dispatchers.network) {
      val response = Try { queueApi.queue(LibraryPopup.NOW, listOf(path)).await() }
        .toEither()
        .fold<RadioUiMessages>({
          RadioUiMessages.NetworkError
        }, { response ->
          if (response.code == 200) {
            RadioUiMessages.QueueSuccess
          } else {
            RadioUiMessages.QueueFailed
          }
        })

      emit(response)
    }
  }
}