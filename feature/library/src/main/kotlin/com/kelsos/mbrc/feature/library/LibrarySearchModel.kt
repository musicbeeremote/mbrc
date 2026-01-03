package com.kelsos.mbrc.feature.library

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

private const val SEARCH_DEBOUNCE_MS = 300L

class LibrarySearchModel {
  private val _term: MutableStateFlow<String> = MutableStateFlow("")

  /**
   * Emits the search term with debouncing for user input.
   */
  @OptIn(FlowPreview::class)
  val term: Flow<String> = _term
    .debounce(SEARCH_DEBOUNCE_MS)
    .distinctUntilChanged()

  suspend fun setTerm(value: String) {
    _term.emit(value)
  }
}
