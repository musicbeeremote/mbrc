package com.kelsos.mbrc.ui.navigation.library.album_tracks

import com.kelsos.mbrc.library.tracks.Track
import com.kelsos.mbrc.domain.AlbumInfo
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface AlbumTracksView : BaseView {
  fun update(cursor: FlowCursorList<Track>)
}

interface AlbumTracksPresenter : Presenter<AlbumTracksView> {
  fun load(album: AlbumInfo)
}

