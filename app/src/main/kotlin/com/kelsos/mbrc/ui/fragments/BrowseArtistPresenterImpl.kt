package com.kelsos.mbrc.ui.fragments

import com.kelsos.mbrc.presenters.BasePresenter
import javax.inject.Inject

class BrowseArtistPresenterImpl
@Inject constructor() :
    BasePresenter<BrowseArtistView>(),
    BrowseArtistPresenter {
  override fun load() {
    throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun reload() {
    throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}
