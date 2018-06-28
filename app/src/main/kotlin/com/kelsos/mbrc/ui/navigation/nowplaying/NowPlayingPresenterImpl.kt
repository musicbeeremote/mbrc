package com.kelsos.mbrc.ui.navigation.nowplaying

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.nowplaying.NowPlayingEntity
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.kelsos.mbrc.utilities.nonNullObserver
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign


class NowPlayingPresenterImpl

constructor(
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  private val repository: NowPlayingRepository,
  private val moveManager: MoveManager,
  private val appRxSchedulers: AppRxSchedulers,
  private val userActionUseCase: UserActionUseCase
) : BasePresenter<NowPlayingView>(), NowPlayingPresenter {

  init {
    moveManager.onMoveSubmit { originalPosition, finalPosition ->
      val data = NowPlayingMoveRequest(originalPosition, finalPosition)
      userActionUseCase.perform(UserAction(Protocol.NowPlayingListMove, data))
    }

    playingTrackLiveDataProvider.observe(this) {
      view().trackChanged(it)
    }
  }

  private lateinit var nowPlayingTracks: LiveData<PagedList<NowPlayingEntity>>

  override fun reload(scrollToTrack: Boolean) {
    disposables += repository.getRemote()
      .subscribeOn(appRxSchedulers.network)
      .observeOn(appRxSchedulers.main)
      .doFinally { view().loading() }
      .subscribe({

      }) {
        with(view()) {
          failure(it)
        }
      }
  }

  private fun onNowPlayingTracksLoaded(it: DataSource.Factory<Int, NowPlayingEntity>) {
    nowPlayingTracks = it.paged()
    nowPlayingTracks.nonNullObserver(this) {
      view().update(it)
    }
  }

  override fun load() {
    disposables += repository.getAll()
      .subscribeOn(appRxSchedulers.database)
      .observeOn(appRxSchedulers.main)
      .doFinally { view().loading() }
      .subscribe({
        onNowPlayingTracksLoaded(it)
      }) {
        view().failure(it)
      }
  }

  override fun search(query: String) {
    // todo: drop and upgrade to do this locally,
    // bus.post(UserAction(Protocol.NowPlayingListSearch, query.trim { it <= ' ' }))
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