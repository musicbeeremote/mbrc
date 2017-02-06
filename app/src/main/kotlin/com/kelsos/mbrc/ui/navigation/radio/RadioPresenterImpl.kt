package com.kelsos.mbrc.ui.navigation.radio

import com.kelsos.mbrc.helper.QueueHandler
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.RadioRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@RadioActivity.Presenter
class RadioPresenterImpl
@Inject
constructor(
  private val radioRepository: RadioRepository,
  private val queue: QueueHandler
) : BasePresenter<RadioView>(), RadioPresenter {

  override fun load() {
    scope.launch {
      try {
        view?.update(radioRepository.getAndSaveRemote())
      } catch (e: Exception) {
        view?.error(e)
      }
    }
  }

  override fun refresh() {
    scope.launch {
      try {
        view?.update(radioRepository.getAndSaveRemote())
      } catch (e: Exception) {
        view?.error(e)
      }
    }
  }

  override fun play(path: String) {
    scope.launch {
      val (success, _) = queue.queuePath(path)
      if (success) {
        view?.radioPlaySuccessful()
      } else {
        view?.radioPlayFailed()
      }
    }
  }
}
