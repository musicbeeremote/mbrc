package com.kelsos.mbrc.repository.data

import rx.Observable

interface RemoteDataSource<T> {
  /**
   * Retrieves all the available data from a remote data source
   */
  fun fetch(): Observable<List<T>>

  companion object {
    const val LIMIT = 400
  }
}
