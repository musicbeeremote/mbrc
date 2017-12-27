package com.kelsos.mbrc.ui.navigation.playlists

import androidx.paging.PagingData
import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface PlaylistView : BaseView {
  suspend fun update(data: PagingData<Playlist>)
  fun failure(throwable: Throwable)
  fun showLoading()
  fun hideLoading()
}

interface PlaylistPresenter : Presenter<PlaylistView> {
  fun load()
  fun reload()
  fun play(path: String)
}
