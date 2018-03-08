package com.kelsos.mbrc.ui.navigation.library.search

import toothpick.config.Module

class SearchResultsModule : Module() {
  init {
    bind(SearchResultsPresenter::class.java).to(SearchResultsPresenterImpl::class.java).singletonInScope()
  }
}