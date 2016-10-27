package com.kelsos.mbrc.ui.navigation.library.albums

import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.AlbumRepository
import timber.log.Timber
import javax.inject.Inject

class BrowseAlbumPresenterImpl
@Inject constructor(private val repository: AlbumRepository) :
    BasePresenter<BrowseAlbumView>(),
    BrowseAlbumPresenter {
  override fun load() {
    addSubcription(repository.getAllCursor().subscribe ({
      view?.update(it)
    }) {
      Timber.v(it)
    })
  }
}
