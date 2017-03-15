package com.kelsos.mbrc.ui.navigation.playlists

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.Playlist
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.PlaylistRepository
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Scheduler
import rx.Single
import javax.inject.Inject
import javax.inject.Named

class PlaylistPresenterImpl
@Inject constructor(
    private val bus: RxBus,
    private val repository: PlaylistRepository,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("main") private val mainScheduler: Scheduler
) :
    BasePresenter<PlaylistView>(),
    PlaylistPresenter {

  override fun load() {
    view?.showLoading()
    addSubcription(repository.getAllCursor().compose { schedule(it) }
        .subscribe({
          view?.update(it)
          view?.hideLoading()
        }) {
          view?.failure(it)
          view?.hideLoading()
        })
  }

  override fun play(path: String) {
    bus.post(MessageEvent.action(UserAction(Protocol.PlaylistPlay, path)))
  }

  override fun reload() {
    view?.showLoading()
    addSubcription(repository.getAndSaveRemote()
        .compose { schedule(it) }
        .subscribe({
          view?.update(it)
          view?.hideLoading()
        }) {
          view?.failure(it)
          view?.hideLoading()
        })
  }

  private fun schedule(it: Single<FlowCursorList<Playlist>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)
}
