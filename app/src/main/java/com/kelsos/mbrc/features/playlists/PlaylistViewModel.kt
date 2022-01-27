package com.kelsos.mbrc.features.playlists

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

interface IPlaylistActions {
  fun play(path: String)

  fun reload()
}

class PlaylistActions(
  private val scope: CoroutineScope,
  private val dispatchers: AppCoroutineDispatchers,
  private val repository: PlaylistRepository,
  private val userActionUseCase: UserActionUseCase,
  private val emit: suspend (uiMessage: PlaylistUiMessages) -> Unit,
) : IPlaylistActions {
  override fun play(path: String) {
    scope.launch(dispatchers.network) {
      userActionUseCase.perform(UserAction(Protocol.PlaylistPlay, path))
    }
  }

  override fun reload() {
    scope.launch(dispatchers.network) {
      val message =
        repository
          .getRemote()
          .fold(
            {
              PlaylistUiMessages.RefreshFailed
            },
            {
              PlaylistUiMessages.RefreshSuccess
            },
          )
      emit(message)
    }
  }
}

class PlaylistViewModel(
  repository: PlaylistRepository,
  dispatchers: AppCoroutineDispatchers,
  userActionUseCase: UserActionUseCase,
) : BaseViewModel<PlaylistUiMessages>() {
  val playlists: Flow<PagingData<Playlist>> = repository.getAll().cachedIn(viewModelScope)
  val actions: PlaylistActions =
    PlaylistActions(
      scope = viewModelScope,
      dispatchers = dispatchers,
      repository = repository,
      userActionUseCase = userActionUseCase,
      emit = this::emit,
    )
}
