package com.kelsos.mbrc.ui.navigation.library.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.kelsos.mbrc.content.library.artists.ArtistEntity
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.preferences.SettingsManager
import com.kelsos.mbrc.utilities.AppRxSchedulers
import com.kelsos.mbrc.utilities.paged
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class BrowseArtistPresenterImpl
@Inject
constructor(
  private val repository: ArtistRepository,
  private val settingsManager: SettingsManager,
  private val appRxSchedulers: AppRxSchedulers
) : BasePresenter<BrowseArtistView>(),
  BrowseArtistPresenter {

  private lateinit var artists: LiveData<PagedList<ArtistEntity>>
  private lateinit var indexes: LiveData<List<String>>

  override fun load() {
    val artistObservable = settingsManager.shouldDisplayOnlyAlbumArtists().flatMap {
      if (it) {
        return@flatMap repository.albumArtists()
      } else {
        return@flatMap repository.allArtists()
      }
    }
    disposables += artistObservable
      .subscribeOn(appRxSchedulers.database)
      .observeOn(appRxSchedulers.main)
      .doFinally { view().hideLoading() }
      .subscribe({
        onArtistsLoaded(it.factory)
        onIndexesLoaded(it.indexes)
      }, {
        Timber.v(it, "Error while loading the data from the database")
      })
  }

  private fun onIndexesLoaded(data: LiveData<List<String>>) {
    if (::indexes.isInitialized) {
      indexes.removeObservers(this)
    }

    indexes = data.apply {
      observe(this@BrowseArtistPresenterImpl, Observer {
        if (it == null) {
          return@Observer
        }
        view().updateIndexes(it)
      })
    }
  }

  private fun onArtistsLoaded(it: DataSource.Factory<Int, ArtistEntity>) {
    if (::artists.isInitialized) {
      artists.removeObservers(this)
    }

    artists = it.paged()
    artists.observe(this, Observer {
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
        Timber.v(it, "Error retrieving the data")
      })
  }
}