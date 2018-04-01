package com.kelsos.mbrc.ui.navigation.nowplaying

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.nowplaying.NowPlaying
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NowPlayingPresenterImpl
@Inject
constructor(
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  private val repository: NowPlayingRepository,
  private val moveManager: MoveManager,
  private val userActionUseCase: UserActionUseCase
) : BasePresenter<NowPlayingView>(), NowPlayingPresenter {

  private lateinit var nowPlayingTracks: Flow<PagingData<NowPlaying>>

  init {
    moveManager.onMoveSubmit { originalPosition, finalPosition ->
      val data = NowPlayingMoveRequest(originalPosition, finalPosition)
      userActionUseCase.perform(UserAction(Protocol.NowPlayingListMove, data))
    }

    playingTrackLiveDataProvider.get().observe(this) {
      if (it == null) {
        return@observe
      }
      view().trackChanged(it)
    }
  }

  override fun reload(scrollToTrack: Boolean) {
    view().showLoading()
    scope.launch {
      try {
        onNowPlayingTracksLoaded(repository.getAndSaveRemote())
      } catch (e: Exception) {
        view().failure(e)
      }
      view().hideLoading()
    }
  }

  private fun onNowPlayingTracksLoaded(data: Flow<PagingData<NowPlaying>>) {
    nowPlayingTracks = data.cachedIn(scope)
    scope.launch {
      data.collectLatest { view().update(it) }
    }
  }

  override fun load() {
    view().showLoading()
    scope.launch {
      try {
        onNowPlayingTracksLoaded(repository.getAll())
      } catch (e: Exception) {
        view().failure(e)
      }
      view().hideLoading()
    }
  }

  override fun search(query: String) {
    // todo: drop and upgrade to do this locally, bus.post(
  }

  override fun moveTrack(from: Int, to: Int) {
    moveManager.move(from, to)
  }

  override fun play(position: Int) {
    userActionUseCase.perform(UserAction(Protocol.NowPlayingListPlay, position))
  }

  override fun removeTrack(position: Int) {
    userActionUseCase.perform(UserAction(Protocol.NowPlayingListRemove, position))
  }
}
