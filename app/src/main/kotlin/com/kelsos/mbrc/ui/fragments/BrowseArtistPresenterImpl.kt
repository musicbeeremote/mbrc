package com.kelsos.mbrc.ui.fragments

import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.ArtistRepository
import timber.log.Timber
import javax.inject.Inject

class BrowseArtistPresenterImpl
@Inject constructor(private val repository: ArtistRepository) :
    BasePresenter<BrowseArtistView>(),
    BrowseArtistPresenter {
  override fun load() {
    addSubcription(repository.getAllCursor().subscribe({
      view?.update(it)
    }, {
      Timber.v(it, "Error while loading the data from the database")
    }))

  }

  override fun reload() {
    addSubcription(repository.getAndSaveRemote().subscribe({
      view?.update(it)
    }, {
      Timber.v(it, "Error retrieving the data")
    }))
  }

}
