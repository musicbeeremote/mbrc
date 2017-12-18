package com.kelsos.mbrc.ui.navigation.playlists

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.kelsos.mbrc.content.playlists.Playlist
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

  private lateinit var playlists: LiveData<PagedList<Playlist>>

  override fun load() {
    scope.launch {
      view().showLoading()
      try {
        onPlaylistsLoad(repository.getAll())
      } catch (e: Exception) {
        view().failure(e)
      }
      view().hideLoading()
    }
  }

  private fun onPlaylistsLoad(it: DataSource.Factory<Int, Playlist>) {
    playlists = it.paged()
    playlists.observe(
      this@PlaylistPresenterImpl,
      {
        if (it != null) {
          view().update(it)
        }
      }
    )
  }

  override fun play(path: String) {
    bus.post(UserAction(Protocol.PlaylistPlay, path))
  }

  override fun reload() {
    view().showLoading()
    scope.launch {
      try {
        onPlaylistsLoad(repository.getAndSaveRemote())
      } catch (e: Exception) {
        view().failure(e)
      }
      view().hideLoading()
    }
  }
}
