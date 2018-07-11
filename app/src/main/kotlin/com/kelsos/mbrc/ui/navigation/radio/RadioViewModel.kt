package com.kelsos.mbrc.ui.navigation.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStationEntity
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking

class RadioViewModel(
  private val radioRepository: RadioRepository,
  private val queueApi: QueueApi,
  private val appRxSchedulers: AppRxSchedulers,
  private val dispatchers: AppCoroutineDispatchers
) : ViewModel() {

  val radios: LiveData<PagedList<RadioStationEntity>> = runBlocking(dispatchers.disk) {
    radioRepository.getAll()
  }.paged()

  fun refresh() {
    async(dispatchers.network) {
      radioRepository.getRemote()
    }
  }

  fun play(path: String) {

  }
}