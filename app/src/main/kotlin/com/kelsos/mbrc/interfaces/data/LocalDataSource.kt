package com.kelsos.mbrc.interfaces.data

import io.reactivex.Observable
import io.reactivex.Single

interface LocalDataSource<T : Data> {
  fun deleteAll()
  fun saveAll(list: List<T>)
  fun loadAllCursor(): Observable<List<T>>
  fun search(term: String): Single<List<T>>
  fun isEmpty(): Single<Boolean>
}
