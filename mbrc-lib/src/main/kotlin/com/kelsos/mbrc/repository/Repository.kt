package com.kelsos.mbrc.repository

import rx.Observable

interface Repository<T> {
  fun getPageObservable(offset: Int, limit: Int): Observable<List<T>>

  val allObservable: Observable<List<T>>

  fun getPage(offset: Int, limit: Int): List<T>

  val all: List<T>

  fun getById(id: Long): T

  fun save(items: List<T>)

  fun save(item: T)

  fun count(): Long
}
