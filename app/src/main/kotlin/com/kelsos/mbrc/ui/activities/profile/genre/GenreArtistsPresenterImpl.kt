package com.kelsos.mbrc.ui.activities.profile.genre

import com.kelsos.mbrc.presenters.BasePresenter
import com.kelsos.mbrc.repository.ArtistRepository
import timber.log.Timber
import javax.inject.Inject

class GenreArtistsPresenterImpl
@Inject constructor(private var repository: ArtistRepository) :
    BasePresenter<GenreArtistsView>(),
    GenreArtistsPresenter {
  override fun load(genre: String) {
    addSubcription(repository.getArtistByGenre(genre).subscribe ({
      view?.update(it)
    }) {
      Timber.v(it)
    })
  }

}
