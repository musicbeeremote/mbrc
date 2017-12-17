package com.kelsos.mbrc.ui.navigation.library.genreartists

import android.arch.lifecycle.Observer
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.paged
import timber.log.Timber
import javax.inject.Inject

class GenreArtistsPresenterImpl
@Inject
constructor(
    private var repository: ArtistRepository
) : BasePresenter<GenreArtistsView>(),
    GenreArtistsPresenter {
  override fun load(genre: String) {
    addDisposable(repository.getArtistByGenre(genre).subscribe({
      val liveData = it.paged()
      liveData.observe(this, Observer {
        if (it != null) {
          view().update(it)
        }
      })
    }) {
      Timber.v(it)
    })
  }

}
