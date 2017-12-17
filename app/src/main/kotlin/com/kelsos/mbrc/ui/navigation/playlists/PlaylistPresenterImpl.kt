package com.kelsos.mbrc.ui.navigation.playlists

import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaylistPresenterImpl
@Inject
constructor(
  private val bus: RxBus,
  private val repository: PlaylistRepository
) : BasePresenter<PlaylistView>(),
  PlaylistPresenter {

  override fun load() {
    scope.launch {
      view().showLoading()
      try {
        val data = repository.getAll()
        val liveData = data.paged()
        liveData.observe(
          this@PlaylistPresenterImpl,
          {
            if (it != null) {
              view().update(it)
            }
          }
        )
      } catch (e: Exception) {
        view().failure(e)
      }
      view().hideLoading()
    }
  }

  override fun play(path: String) {
    bus.post(UserAction(Protocol.PlaylistPlay, path))
  }

  override fun reload() {
    view().showLoading()
    scope.launch {
      try {
        val data = repository.getAndSaveRemote()
        val liveData = data.paged()
        liveData.observe(
          this@PlaylistPresenterImpl,
          {
            if (it != null) {
              view().update(it)
            }
          }
        )
      } catch (e: Exception) {
        view().failure(e)
      }
      view().hideLoading()
    }
  }
}
