package com.kelsos.mbrc.ui.navigation.radio

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RadioPresenterImpl(
  private val radioRepository: RadioRepository,
  private val queue: QueueHandler,
  dispatcher: AppCoroutineDispatchers
) : BasePresenter<RadioView>(dispatcher.main), RadioPresenter {

  private lateinit var radios: Flow<PagingData<RadioStation>>

  override fun load() {
    view().loading(true)
    scope.launch {
      try {
        onRadiosLoaded(radioRepository.getAndSaveRemote())
      } catch (e: Exception) {
        view().error(e)
      }
      view().loading(false)
    }
  }

  private fun onRadiosLoaded(data: Flow<PagingData<RadioStation>>) {
    this.radios = data.cachedIn(scope)
    scope.launch {
      data.collectLatest { view().update(it) }
    }
  }

  override fun refresh() {
    view().loading(true)
    scope.launch {
      try {
        onRadiosLoaded(radioRepository.getAndSaveRemote())
      } catch (e: Exception) {
        view().error(e)
      }
      view().loading(false)
    }
  }

  override fun play(path: String) {
    scope.launch {
      val (success, _) = queue.queuePath(path)
      if (success) {
        view().radioPlaySuccessful()
      } else {
        view().radioPlayFailed()
      }
    }
  }
}
