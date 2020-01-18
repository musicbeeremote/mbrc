package com.kelsos.mbrc.features.nowplaying.presentation

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.common.utilities.paged
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackState
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
import kotlinx.coroutines.launch

class NowPlayingViewModel(
  dispatchers: AppCoroutineDispatchers,
  val trackState: PlayingTrackState,
  private val repository: NowPlayingRepository,
  private val moveManager: MoveManager,
  private val userActionUseCase: UserActionUseCase
) : BaseViewModel<NowPlayingUiMessages>(dispatchers) {

  val list: LiveData<PagedList<NowPlaying>> = repository.getAll().paged()

  init {
    moveManager.onMoveCommit { originalPosition, finalPosition ->
      userActionUseCase.moveTrack(
        NowPlayingMoveRequest(
          originalPosition,
          finalPosition
        )
      )
    }
  }

  fun reload() {
    scope.launch {
      val result = repository.getRemote()
        .fold({
          NowPlayingUiMessages.RefreshFailed
        }, {
          NowPlayingUiMessages.RefreshSuccess
        })
      emit(result)
    }
  }

  fun search(query: String) {
    scope.launch {
      val position = repository.findPosition(query)
      if (position > 0) {
        play(position)
      }
    }
  }

  fun moveTrack(from: Int, to: Int) {
    scope.launch {
      moveManager.move(from, to)
    }
  }

  fun play(position: Int) {
    userActionUseCase.playTrack(position + 1)
  }

  fun removeTrack(position: Int) {
    scope.launch {
      delay(400)
      userActionUseCase.removeTrack(position)
    }
  }

  fun move() {
    scope.launch {
      moveManager.commit()
    }
  }
}