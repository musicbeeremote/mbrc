package com.kelsos.mbrc.ui.navigation.radio

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStation
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@RadioActivity.Presenter
class RadioPresenterImpl
@Inject
constructor(
  private val radioRepository: RadioRepository,
  private val queue: QueueHandler,
  dispatcher: AppDispatchers
) : BasePresenter<RadioView>(dispatcher.main), RadioPresenter {

  private lateinit var radios: Flow<PagingData<RadioStation>>

  override fun load() {
    view().showLoading()
    scope.launch {
      try {
        onRadiosLoaded(radioRepository.getAndSaveRemote())
      } catch (e: Exception) {
        view().error(e)
      }
      view().hideLoading()
    }
  }

  private fun onRadiosLoaded(data: Flow<PagingData<RadioStation>>) {
    this.radios = data.cachedIn(scope)
    scope.launch {
      data.collectLatest { view().update(it) }
    }
  }

  override fun refresh() {
    view().showLoading()
    scope.launch {
      try {
        onRadiosLoaded(radioRepository.getAndSaveRemote())
      } catch (e: Exception) {
        view().error(e)
      }
      view().hideLoading()
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
