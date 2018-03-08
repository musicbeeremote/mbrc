package com.kelsos.mbrc.ui.navigation.playlists

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.kelsos.mbrc.content.playlists.PlaylistEntity
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.paged
import javax.inject.Inject

class PlaylistPresenterImpl
@Inject
constructor(
  private val bus: RxBus,
  private val repository: PlaylistRepository,
  private val schedulerProvider: SchedulerProvider
) : BasePresenter<PlaylistView>(),
    PlaylistPresenter {

  private lateinit var playlists: LiveData<PagedList<PlaylistEntity>>

  override fun load() {
    view().showLoading()
    addDisposable(repository.getAll()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          onPlaylistsLoad(it)
          view().hideLoading()
        }) {
          view().failure(it)
          view().hideLoading()
        })
  }

  private fun onPlaylistsLoad(it: DataSource.Factory<Int, PlaylistEntity>) {
    playlists = it.paged()
    playlists.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun play(path: String) {
    bus.post(UserAction(Protocol.PlaylistPlay, path))
  }

  override fun reload() {
    view().showLoading()
    addDisposable(repository.getAndSaveRemote()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          onPlaylistsLoad(it)
          view().hideLoading()
        }) {
          view().failure(it)
          view().hideLoading()
        })
  }
}