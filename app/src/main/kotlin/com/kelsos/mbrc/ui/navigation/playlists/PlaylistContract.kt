package com.kelsos.mbrc.ui.navigation.playlists

import com.kelsos.mbrc.content.playlists.Playlist
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface PlaylistView : BaseView {
  fun update(cursor: List<Playlist>)
  fun failure(throwable: Throwable)
  fun showLoading()
  fun hideLoading()
}

interface PlaylistPresenter : Presenter<PlaylistView> {
  fun load()
  fun reload()
  fun play(path: String)
}