package com.kelsos.mbrc.ui.navigation.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.utilities.paged
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class PlaylistViewModel(
  private val repository: PlaylistRepository,
  private val dispatchers: AppCoroutineDispatchers,
  private val userActionUseCase: UserActionUseCase
) : ViewModel(), CoroutineScope {
  override val coroutineContext: CoroutineContext = dispatchers.network

  val playlists: LiveData<PagedList<Playlist>> = repository.getAll().paged()

  fun play(path: String) {
    userActionUseCase.perform(UserAction(Protocol.PlaylistPlay, path))
  }

  fun reload() {
    launch(dispatchers.network) {
      repository.getRemote()
    }
  }
}