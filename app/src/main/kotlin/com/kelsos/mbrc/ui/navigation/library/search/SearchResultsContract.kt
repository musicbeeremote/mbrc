package com.kelsos.mbrc.ui.navigation.library.search

import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface SearchResultsView : BaseView {
  fun update(searchResults: SearchResults)
}

interface SearchResultsPresenter : Presenter<SearchResultsView> {
  fun search(term: String)
}
