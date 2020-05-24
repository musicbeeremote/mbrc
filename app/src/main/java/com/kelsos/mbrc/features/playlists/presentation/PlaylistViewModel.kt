package com.kelsos.mbrc.features.playlists.presentation

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.ui.BaseViewModel
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PlaylistViewModel(
  dispatchers: AppCoroutineDispatchers,
  private val repository: PlaylistRepository,
  private val userActionUseCase: UserActionUseCase
) : BaseViewModel<PlaylistUiMessages>(dispatchers), CoroutineScope {
  override val coroutineContext: CoroutineContext = dispatchers.network

  val playlists: LiveData<PagedList<Playlist>> = repository.getAll().paged()

  fun play(path: String) {
    userActionUseCase.perform(UserAction(Protocol.PlaylistPlay, path))
  }

  fun reload() {
    scope.launch {
      val message = repository.getRemote()
        .fold({
          PlaylistUiMessages.RefreshFailed
        }, {
          PlaylistUiMessages.RefreshSuccess
        })
      emit(message)
    }
  }
}