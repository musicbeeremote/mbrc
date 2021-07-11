package com.kelsos.mbrc.features.playlists.presentation

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaylistViewModel(
  private val repository: PlaylistRepository,
  private val dispatchers: AppCoroutineDispatchers,
  private val userActionUseCase: UserActionUseCase
) : BaseViewModel<PlaylistUiMessages>() {

  val playlists: Flow<PagingData<Playlist>> = repository.getAll()

  fun play(path: String) {
    viewModelScope.launch(dispatchers.network) {
      userActionUseCase.perform(UserAction(Protocol.PlaylistPlay, path))
    }
  }

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      val message = repository.getRemote()
        .fold(
          {
            PlaylistUiMessages.RefreshFailed
          },
          {
            PlaylistUiMessages.RefreshSuccess
          }
        )
      emit(message)
    }
  }
}
