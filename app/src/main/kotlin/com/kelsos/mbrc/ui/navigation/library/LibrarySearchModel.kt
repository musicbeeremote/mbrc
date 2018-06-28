package com.kelsos.mbrc.ui.navigation.library

import kotlinx.coroutines.flow.MutableStateFlow

class LibrarySearchModel {
  val term: MutableStateFlow<String> = MutableStateFlow("")
}
