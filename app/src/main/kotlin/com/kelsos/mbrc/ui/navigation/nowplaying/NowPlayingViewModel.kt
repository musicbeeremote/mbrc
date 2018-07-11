package com.kelsos.mbrc.ui.navigation.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.nowplaying.NowPlaying
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepository
import com.kelsos.mbrc.events.Event
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NowPlayingViewModel(
  val playingTrack: PlayingTrackLiveDataProvider,
  private val repository: NowPlayingRepository,
  private val moveManager: MoveManager,
  private val dispatchers: AppCoroutineDispatchers,
  private val userActionUseCase: UserActionUseCase
) : ViewModel() {
  private val eventStream: MutableSharedFlow<Event<Int>> = MutableSharedFlow(0, 5)

  val nowPlayingTracks: Flow<PagingData<NowPlaying>> = repository.getAll()
  val events: SharedFlow<Event<Int>>
    get() = eventStream

  init {
    moveManager.onMoveSubmit { originalPosition, finalPosition ->
      val data = NowPlayingMoveRequest(originalPosition, finalPosition)
      userActionUseCase.perform(UserAction(Protocol.NowPlayingListMove, data))
    }
  }

  fun refresh() {
    viewModelScope.launch {
      try {
        repository.getRemote()
      } catch (ex: Exception) {
        withContext(dispatchers.main) {
          eventStream.tryEmit(Event(0))
        }
      }
    }
  }

  fun moveTrack(from: Int, to: Int) {
    viewModelScope.launch {
      moveManager.move(from, to)
    }
  }

  fun play(position: Int) {
    userActionUseCase.perform(UserAction(Protocol.NowPlayingListPlay, position))
  }

  fun removeTrack(position: Int) {
    userActionUseCase.perform(UserAction(Protocol.NowPlayingListRemove, position))
  }
}
