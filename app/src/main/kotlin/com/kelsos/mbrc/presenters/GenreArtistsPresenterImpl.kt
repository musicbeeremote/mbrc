package com.kelsos.mbrc.presenters

import javax.inject.Inject
import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.interactors.library.GenreArtistInteractor
import com.kelsos.mbrc.ui.views.GenreArtistView
import timber.log.Timber

class GenreArtistsPresenterImpl : GenreArtistsPresenter {
  @Inject private lateinit var genreArtistInteractor: GenreArtistInteractor
  @Inject private lateinit var interactor: QueueInteractor

  private var view: GenreArtistView? = null

  override fun bind(view: GenreArtistView) {
    this.view = view
  }

  override fun onPause() {

  }

  override fun onResume() {

  }

  override fun load(genreId: Long) {
    genreArtistInteractor.getGenreArtists(genreId).subscribe({ view!!.update(it) }) {
      Timber.v(it, "Failed")
    }
  }

  override fun queue(@Queue.Action action: String, artist: Artist) {
    interactor.execute(MetaDataType.ARTIST, action, artist.id).subscribe({
      if (it) {
        view?.onQueueSuccess()
      } else {
        view?.onQueueFailure()
      }
    }) {
      Timber.e(it, "Queueing failed")
      view?.onQueueFailure()
    }
  }
}
