package com.kelsos.mbrc.features.radio.presentation

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.content.nowplaying.queue.LibraryPopup
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.features.radio.domain.RadioStation
import com.kelsos.mbrc.features.radio.repository.RadioRepository
import com.kelsos.mbrc.ui.BaseViewModel
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.launch

class RadioViewModel(
  private val radioRepository: RadioRepository,
  private val queueApi: QueueApi,
  private val dispatchers: AppCoroutineDispatchers
) : BaseViewModel<RadioRefreshResult>(dispatchers) {

  val radios: LiveData<PagedList<RadioStation>> = radioRepository.getAll().paged()

  fun reload() {
    scope.launch(dispatchers.network) {
      radioRepository.getRemote()
        .toEither()
        .fold({
          RadioRefreshResult.RefreshFailed
        }, {
          RadioRefreshResult.RefreshSuccess
        })
    }
  }

  fun play(path: String) {
    scope.launch(dispatchers.network) {
      queueApi.queue(LibraryPopup.NOW, listOf(path))
    }
  }
}