package com.kelsos.mbrc.feature.content.playlists

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kelsos.mbrc.core.common.mvvm.BaseViewModel
import com.kelsos.mbrc.core.common.mvvm.UiMessageBase
import com.kelsos.mbrc.core.common.state.ConnectionStateFlow
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.core.data.playlist.PlaylistRepository
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import com.kelsos.mbrc.core.networking.protocol.usecases.UserActionUseCase
import com.kelsos.mbrc.core.networking.protocol.usecases.performUserAction
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class PlaylistUiMessages : UiMessageBase {
  data object RefreshFailed : PlaylistUiMessages()

  data object RefreshSuccess : PlaylistUiMessages()

  data object NetworkUnavailable : PlaylistUiMessages()

  data object PlayFailed : PlaylistUiMessages()
}

interface IPlaylistActions {
  fun play(url: String)
  fun reload()
  fun reload(showUserMessage: Boolean)
  fun navigateToFolder(path: String)
  fun navigateUp(): Boolean
}

class PlaylistActions(
  private val scope: CoroutineScope,
  private val dispatchers: AppCoroutineDispatchers,
  private val repository: PlaylistRepository,
  private val userActionUseCase: UserActionUseCase,
  private val connectionStateFlow: ConnectionStateFlow,
  private val emit: suspend (uiMessage: PlaylistUiMessages) -> Unit,
  private val getCurrentPath: () -> String,
  private val setCurrentPath: (String) -> Unit
) : IPlaylistActions {
  override fun play(url: String) {
    scope.launch(dispatchers.network) {
      val result =
        if (!connectionStateFlow.isConnected) {
          PlaylistUiMessages.NetworkUnavailable
        } else {
          try {
            userActionUseCase.performUserAction(Protocol.PlaylistPlay, url)
            return@launch // Don't emit anything on success for play action
          } catch (e: IOException) {
            Timber.e(e)
            PlaylistUiMessages.PlayFailed
          }
        }
      emit(result)
    }
  }

  override fun reload() {
    reload(showUserMessage = true)
  }

  override fun reload(showUserMessage: Boolean) {
    scope.launch(dispatchers.network) {
      if (!connectionStateFlow.isConnected) {
        if (showUserMessage) {
          emit(PlaylistUiMessages.NetworkUnavailable)
        }
        return@launch
      }

      val result =
        try {
          repository.getRemote()
          if (showUserMessage) PlaylistUiMessages.RefreshSuccess else null
        } catch (e: IOException) {
          Timber.e(e)
          if (showUserMessage) PlaylistUiMessages.RefreshFailed else null
        }

      result?.let { emit(it) }
    }
  }

  override fun navigateToFolder(path: String) {
    setCurrentPath(path)
  }

  override fun navigateUp(): Boolean {
    val parentPath = getParentPath(getCurrentPath())
    return if (parentPath != null) {
      setCurrentPath(parentPath)
      true
    } else {
      false
    }
  }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistViewModel(
  repository: PlaylistRepository,
  dispatchers: AppCoroutineDispatchers,
  userActionsUseCase: UserActionUseCase,
  connectionStateFlow: ConnectionStateFlow
) : BaseViewModel<PlaylistUiMessages>() {

  private val _currentPath = MutableStateFlow("")
  val currentPath: StateFlow<String> = _currentPath.asStateFlow()

  /** Paginated browser items that react to path changes */
  val items = _currentPath
    .flatMapLatest { path -> repository.getBrowserItemsAtPath(path) }
    .cachedIn(viewModelScope)

  val actions: PlaylistActions =
    PlaylistActions(
      scope = viewModelScope,
      dispatchers = dispatchers,
      repository = repository,
      userActionUseCase = userActionsUseCase,
      connectionStateFlow = connectionStateFlow,
      emit = this::emit,
      getCurrentPath = { _currentPath.value },
      setCurrentPath = { _currentPath.value = it }
    )
}
