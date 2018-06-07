package com.kelsos.mbrc.ui.navigation.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.kelsos.mbrc.content.playlists.PlaylistEntity
import com.kelsos.mbrc.content.playlists.PlaylistRepository
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.client.UserActionUseCase
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class PlaylistPresenterImpl
@Inject
constructor(
  private val repository: PlaylistRepository,
  private val appRxSchedulers: AppRxSchedulers,
  private val userActionUseCase: UserActionUseCase
) : BasePresenter<PlaylistView>(), PlaylistPresenter {

  private lateinit var playlists: LiveData<PagedList<PlaylistEntity>>

  override fun load() {
    disposables += repository.getAll()
      .subscribeOn(appRxSchedulers.database)
      .observeOn(appRxSchedulers.main)
      .doAfterTerminate { view().hideLoading() }
      .subscribe({
        onPlaylistsLoad(it)
      }) {
        view().failure(it)
      }
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
    userActionUseCase.perform(UserAction(Protocol.PlaylistPlay, path))
  }

  override fun reload() {
    disposables += repository.getRemote()
      .subscribeOn(appRxSchedulers.network)
      .observeOn(appRxSchedulers.main)
      .doAfterTerminate { view().hideLoading() }
      .subscribe({
        ///
      }) {
        view().failure(it)
      }

  }
}