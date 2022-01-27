package com.kelsos.mbrc.features.nowplaying.presentation

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.common.state.AppState
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.features.nowplaying.domain.MoveManager
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying
import com.kelsos.mbrc.features.nowplaying.repository.NowPlayingRepository
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.client.moveTrack
import com.kelsos.mbrc.networking.client.playTrack
import com.kelsos.mbrc.networking.client.removeTrack
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NowPlayingViewModel(
  private val dispatchers: AppCoroutineDispatchers,
  private val repository: NowPlayingRepository,
  private val moveManager: MoveManager,
  private val userActionUseCase: UserActionUseCase,
  appState: AppState,
) : BaseViewModel<NowPlayingUiMessages>() {

  val list: Flow<PagingData<NowPlaying>> = repository.getAll().cachedIn(viewModelScope)
  val playingTracks: Flow<PlayingTrack> = appState.playingTrack

  init {
    moveManager.onMoveCommit { originalPosition, finalPosition ->
      viewModelScope.launch(dispatchers.network) {
        userActionUseCase.moveTrack(
          NowPlayingMoveRequest(
            originalPosition,
            finalPosition
          )
        )
      }
    }
  }

  fun reload() {
    viewModelScope.launch(dispatchers.network) {
      val result = repository.getRemote()
        .fold(
          {
            NowPlayingUiMessages.RefreshFailed
          },
          {
            NowPlayingUiMessages.RefreshSuccess
          }
        )
      emit(result)
    }
  }

  fun search(query: String) {
    viewModelScope.launch(dispatchers.database) {
      val position = repository.findPosition(query)
      if (position > 0) {
        play(position)
      }
    }
  }

  fun moveTrack(from: Int, to: Int) {
    moveManager.move(from, to)
  }

  fun play(position: Int) {
    viewModelScope.launch(dispatchers.network) {
      userActionUseCase.playTrack(position + 1)
    }
  }

  fun removeTrack(position: Int) {
    viewModelScope.launch(dispatchers.network) {
      delay(timeMillis = 400)
      userActionUseCase.removeTrack(position)
    }
  }

  fun move() {
    moveManager.commit()
  }
}
