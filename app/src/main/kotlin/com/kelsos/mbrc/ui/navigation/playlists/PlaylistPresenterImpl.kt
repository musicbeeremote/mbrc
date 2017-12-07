package com.kelsos.mbrc.ui.navigation.playlists

import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Single
import javax.inject.Inject

class PlaylistPresenterImpl
@Inject constructor(
    private val bus: RxBus,
    private val repository: PlaylistRepository,
    private val schedulerProvider: SchedulerProvider
) :
    BasePresenter<PlaylistView>(),
    PlaylistPresenter {

  override fun load() {
    view().showLoading()
    addDisposable(repository.getAllCursor().compose { schedule(it) }
        .subscribe({
          view().update(it)
          view().hideLoading()
        }) {
          view().failure(it)
          view().hideLoading()
        })
  }

  override fun play(path: String) {
    bus.post(UserAction(Protocol.PlaylistPlay, path))
  }

  override fun reload() {
    view().showLoading()
    addDisposable(repository.getAndSaveRemote()
        .compose { schedule(it) }
        .subscribe({
          view().update(it)
          view().hideLoading()
        }) {
          view().failure(it)
          view().hideLoading()
        })
  }

  private fun schedule(it: Single<FlowCursorList<Playlist>>) = it.observeOn(schedulerProvider.main())
      .subscribeOn(schedulerProvider.io())
}
