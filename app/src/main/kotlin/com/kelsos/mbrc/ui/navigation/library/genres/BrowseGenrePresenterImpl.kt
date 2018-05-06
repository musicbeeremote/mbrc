package com.kelsos.mbrc.ui.navigation.library.genres

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.kelsos.mbrc.content.library.genres.GenreEntity
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class BrowseGenrePresenterImpl
@Inject
constructor(
  private val repository: GenreRepository,
  private val appRxSchedulers: AppRxSchedulers
) : BasePresenter<BrowseGenreView>(),
  BrowseGenrePresenter {

  private lateinit var genres: LiveData<PagedList<GenreEntity>>
  private lateinit var indexes: LiveData<List<String>>


  override fun load() {

    disposables += repository.allGenres()
      .subscribeOn(appRxSchedulers.database)
      .observeOn(appRxSchedulers.main)
      .doFinally { view().hideLoading() }
      .subscribe({
        onGenresLoaded(it.factory)
        onIndexesLoaded(it.indexes)
      }, {
        Timber.v(it, "Error while loading the data from the database")
        view().failure(it)
      })
  }

  private fun onIndexesLoaded(data: LiveData<List<String>>) {
    if (::indexes.isInitialized) {
      indexes.removeObservers(this)
    }

    indexes = data.apply {
      observe(this@BrowseGenrePresenterImpl, Observer {
        if (it == null) {
          return@Observer
        }
        view().updateIndexes(it)
      })
    }
  }

  private fun onGenresLoaded(it: DataSource.Factory<Int, GenreEntity>) {
    if (::genres.isInitialized) {
      genres.removeObservers(this)
    }

    genres = it.paged()
    genres.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun reload() {
    disposables += repository.getRemote()
      .subscribeOn(appRxSchedulers.network)
      .observeOn(appRxSchedulers.main)
      .doFinally { view().hideLoading() }
      .subscribe({

      }, {
        Timber.v(it, "Error while loading the data from the database")
        view().failure(it)
      })
  }
}