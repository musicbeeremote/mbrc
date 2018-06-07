package com.kelsos.mbrc.ui.navigation.library.genreartists

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class GenreArtistsPresenterImpl
@Inject
constructor(
  private val repository: ArtistRepository,
  private val appRxSchedulers: AppRxSchedulers
) : BasePresenter<GenreArtistsView>(),
  GenreArtistsPresenter {

  private lateinit var artists: LiveData<PagedList<ArtistEntity>>

  override fun load(genre: String) {
    disposables += repository.getArtistByGenre(genre)
      .subscribeOn(appRxSchedulers.database)
      .observeOn(appRxSchedulers.main)
      .subscribe({
        artists = it.paged()
        artists.observe(this, Observer {
          if (it != null) {
            view().update(it)
          }
        })
      }) {
        Timber.v(it)
      }

  }
}