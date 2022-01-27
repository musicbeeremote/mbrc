package com.kelsos.mbrc.features.library.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LibrarySearchModel {
  private val _search: MutableStateFlow<String> =
    MutableStateFlow(
      "",
    )
  val search: StateFlow<String>
    get() = _search

  fun search(search: String) {
    _search.tryEmit(search)
  }
}
