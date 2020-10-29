package com.kelsos.mbrc.ui.navigation.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject


class LibrarySearchModel
@Inject
constructor() {
  private val _search: MutableLiveData<String> = MutableLiveData()

  fun search(search: String) {
    this._search.postValue(search)
  }

  val term: LiveData<String>
    get() = _search
}
