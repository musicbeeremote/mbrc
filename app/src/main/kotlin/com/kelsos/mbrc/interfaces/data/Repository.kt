package com.kelsos.mbrc.interfaces.data

interface Repository<T : Data> {
  suspend fun getAllCursor(): List<T>
  suspend fun getAndSaveRemote(): List<T>
  suspend fun getRemote()
  suspend fun search(term: String): List<T>
  suspend fun cacheIsEmpty(): Boolean
  suspend fun count(): Long
}
