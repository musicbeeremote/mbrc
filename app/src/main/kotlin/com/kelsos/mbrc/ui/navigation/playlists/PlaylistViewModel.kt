package com.kelsos.mbrc.ui.navigation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaylistViewModel(
  private val repository: PlaylistRepository,
  private val dispatchers: AppCoroutineDispatchers,
  private val userActionUseCase: UserActionUseCase
) : ViewModel() {

  val playlists: Flow<PagingData<Playlist>> = repository.getAll()

  fun play(path: String) {
    userActionUseCase.perform(UserAction(Protocol.PlaylistPlay, path))
  }

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      repository.getRemote()
    }
  }
}
