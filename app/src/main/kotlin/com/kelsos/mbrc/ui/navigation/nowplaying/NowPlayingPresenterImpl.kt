package com.kelsos.mbrc.ui.navigation.nowplaying

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.kelsos.mbrc.content.activestatus.livedata.PlayingTrackLiveDataProvider
import com.kelsos.mbrc.content.nowplaying.NowPlayingEntity
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class NowPlayingPresenterImpl
@Inject constructor(
  playingTrackLiveDataProvider: PlayingTrackLiveDataProvider,
  private val repository: NowPlayingRepository,
  private val moveManager: MoveManager,
  private val schedulerProvider: SchedulerProvider,
  private val userActionUseCase: UserActionUseCase
) : BasePresenter<NowPlayingView>(), NowPlayingPresenter {

  init {
    moveManager.onMoveSubmit { originalPosition, finalPosition ->
      val data = NowPlayingMoveRequest(originalPosition, finalPosition)
      userActionUseCase.perform(UserAction(Protocol.NowPlayingListMove, data))
    }

    playingTrackLiveDataProvider.get().observe(this, Observer {
      if (it == null) {
        return@Observer
      }
      view().trackChanged(it)
    })
  }

  private lateinit var nowPlayingTracks: LiveData<PagedList<NowPlayingEntity>>

  override fun reload(scrollToTrack: Boolean) {
    view().showLoading()
    disposables += repository.getAndSaveRemote()
      .subscribeOn(schedulerProvider.io())
      .observeOn(schedulerProvider.main())
      .subscribe({
        onNowPlayingTracksLoaded(it)

        with(view()) {
          hideLoading()
        }
      }) {
        with(view()) {
          failure(it)
          hideLoading()
        }
      }
  }

  private fun onNowPlayingTracksLoaded(it: DataSource.Factory<Int, NowPlayingEntity>) {
    nowPlayingTracks = it.paged()
    nowPlayingTracks.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun load() {
    disposables += repository.getAll()
      .subscribeOn(schedulerProvider.io())
      .observeOn(schedulerProvider.main())
      .subscribe({
        onNowPlayingTracksLoaded(it)
        view().hideLoading()
      }) {
        view().failure(it)
        view().hideLoading()
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