package com.kelsos.mbrc.ui.navigation.playlists

import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaylistPresenterImpl
@Inject constructor(
  private val bus: RxBus,
  private val repository: PlaylistRepository
) : BasePresenter<PlaylistView>(),
  PlaylistPresenter {

  override fun load() {
    scope.launch {
      view?.showLoading()
      try {
        view?.update(repository.getAllCursor())
      } catch (e: Exception) {
        view?.failure(e)
      }
      view?.hideLoading()
    }
  }

  override fun play(path: String) {
    bus.post(UserAction(Protocol.PlaylistPlay, path))
  }

  override fun reload() {
    view?.showLoading()
    scope.launch {
      try {
        view?.update(repository.getAndSaveRemote())
      } catch (e: Exception) {
        view?.failure(e)
      }
      view?.hideLoading()
    }
  }
}
