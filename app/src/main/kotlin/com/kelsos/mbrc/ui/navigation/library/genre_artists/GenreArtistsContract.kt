package com.kelsos.mbrc.ui.navigation.library.genre_artists

import com.kelsos.mbrc.content.library.artists.Artist
import com.kelsos.mbrc.content.now_playing.queue.Queue
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter
import com.raizlabs.android.dbflow.list.FlowCursorList

interface GenreArtistsView : BaseView {
  fun update(data: FlowCursorList<Artist>)
  fun queue(success: Boolean, tracks: Int)
}

interface GenreArtistsPresenter : Presenter<GenreArtistsView> {
  fun load(genre: String)
  fun queue(@Queue.Action action: String, entry: Artist)
}
