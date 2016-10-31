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
@Inject constructor(private val bus: RxBus,
                    private val repository: PlaylistRepository,
                    @Named("io") private val ioScheduler: Scheduler,
                    @Named("main") private val mainScheduler: Scheduler) :
    BasePresenter<PlaylistView>(),
    PlaylistPresenter {

  override fun load() {
    addSubcription(repository.getAllCursor().compose { schedule(it) }
        .subscribe({
          view?.update(it)
        }) {
          view?.failure(it)
        })
  }

  override fun play(path: String) {
    bus.post(MessageEvent.action(UserAction(Protocol.PlaylistPlay, path)))
  }

  override fun reload() {
    addSubcription(repository.getAndSaveRemote()
        .compose { schedule(it) }
        .subscribe({
          view?.update(it)
        }) {
          view?.failure(it)
        })
  }

  private fun schedule(it: Single<FlowCursorList<Playlist>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)
}
