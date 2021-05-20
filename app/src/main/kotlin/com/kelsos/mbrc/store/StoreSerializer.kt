package com.kelsos.mbrc.store

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object StoreSerializer : Serializer<Store> {
  override val defaultValue: Store
    get() = Store.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): Store {
    try {
      return Store.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: Store, output: OutputStream) = t.writeTo(output)
}

val Context.dataStore: DataStore<Store> by dataStore("cache_store.db", StoreSerializer)
