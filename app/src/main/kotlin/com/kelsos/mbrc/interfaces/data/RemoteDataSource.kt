package com.kelsos.mbrc.interfaces.data

import io.reactivex.Observable

interface RemoteDataSource<T> {
  /**
   * Retrieves all the available data from a remote data source
   */
  fun fetch(): Observable<List<T>>

  companion object {
    const val LIMIT = 800
  }
}
