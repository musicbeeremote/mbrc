package com.kelsos.mbrc.ui.navigation.playlists

import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.kelsos.mbrc.playlists.Playlist
import com.raizlabs.android.dbflow.list.FlowCursorList

interface PlaylistView : BaseView {
  fun update(cursor: FlowCursorList<Playlist>)
  fun failure(throwable: Throwable)
  fun showLoading()
  fun hideLoading()
}

interface PlaylistPresenter : Presenter<PlaylistView> {
  fun load()
  fun reload()
  fun play(path: String)
}
