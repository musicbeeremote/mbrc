package com.kelsos.mbrc.ui.navigation.library.genreartists

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import timber.log.Timber
import javax.inject.Inject

class GenreArtistsPresenterImpl
@Inject
constructor(
    private val repository: ArtistRepository,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<GenreArtistsView>(),
    GenreArtistsPresenter {

  private lateinit var artists: LiveData<List<ArtistEntity>>

  override fun load(genre: String) {
    addDisposable(repository.getArtistByGenre(genre)
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          artists = it
          artists.observe(this, Observer {
            if (it != null) {
              view().update(it)
            }
          })
        }) {
          Timber.v(it)
        })
  }

}
