package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.MetaDataType
import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Artist
import com.kelsos.mbrc.interactors.QueueInteractor
import com.kelsos.mbrc.interactors.library.GenreArtistInteractor
import com.kelsos.mbrc.ui.views.GenreArtistView
import timber.log.Timber
import javax.inject.Inject

class GenreArtistsPresenterImpl
@Inject constructor(private val genreArtistInteractor: GenreArtistInteractor,
                    private val interactor: QueueInteractor) : GenreArtistsPresenter {

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
