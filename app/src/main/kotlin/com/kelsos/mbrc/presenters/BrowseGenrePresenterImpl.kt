package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.constants.Constants
import com.kelsos.mbrc.domain.Genre
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.interactors.library.LibraryGenreInteractor
import com.kelsos.mbrc.interactors.playlists.PlaylistAddInteractor
import com.kelsos.mbrc.ui.views.BrowseGenreView
import timber.log.Timber
import javax.inject.Inject

class BrowseGenrePresenterImpl
@Inject constructor(private val interactor: LibraryGenreInteractor,
                    private val queue: QueueInteractor,
                    private val playlistAddInteractor: PlaylistAddInteractor) : BrowseGenrePresenter {
  private var view: BrowseGenreView? = null

  override fun bind(view: BrowseGenreView) {
    this.view = view
  }

  override fun load() {
    interactor.execute().task().subscribe({
      view?.clear()
      view?.update(it)
    }, { Timber.v(it, "Failed to load") })
  }

  override fun queue(genre: Genre, @Queue.Action action: String) {
    queue.execute(MetaDataType.GENRE,
        action,
        genre.id).task().subscribe({
      if (it!!) {
        view?.showEnqueueSuccess()
      } else {
        view?.showEnqueueFailure()
      }
    }) { view!!.showEnqueueFailure() }
  }

  override fun load(page: Int) {
    interactor.execute(page * Constants.PAGE_SIZE)
        .task()
        .subscribe({ view!!.update(it) }, { Timber.v(it) })
  }

  override fun createPlaylist(selectionId: Long, name: String) {

  }

  override fun playlistAdd(selectionId: Long, playlistId: Long) {

  }
}
