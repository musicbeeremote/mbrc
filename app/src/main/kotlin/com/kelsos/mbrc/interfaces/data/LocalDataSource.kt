package com.kelsos.mbrc.interfaces.data

interface LocalDataSource<T : Data> {
  suspend fun deleteAll()
  suspend fun saveAll(list: List<T>)
  suspend fun loadAllCursor(): List<T>
  suspend fun search(term: String): List<T>
  suspend fun isEmpty(): Boolean
  suspend fun count(): Long
  suspend fun removePreviousEntries(epoch: Long)
}
