package com.kelsos.mbrc.repository

import rx.Observable

interface Repository<T> {
  fun getPageObservable(offset: Int, limit: Int): Observable<List<T>>

  fun getAllObservable(): Observable<List<T>>

  fun getPage(offset: Int, limit: Int): List<T>

  fun getAll(): List<T>

  fun getById(id: Long): T?

  fun save(items: List<T>)

  fun save(item: T)

  fun count(): Long
}
