package com.kelsos.mbrc.features.library.presentation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class LibrarySearchModel {
  private val _search: MutableSharedFlow<String> = MutableSharedFlow(
    1,
    1,
    BufferOverflow.DROP_OLDEST
  )
  val search: SharedFlow<String>
    get() = _search

  fun search(search: String) {
    _search.tryEmit(search)
  }
}