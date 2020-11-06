package com.kelsos.mbrc.store

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object StoreSerializer : Serializer<Store> {
  override fun readFrom(input: InputStream): Store {
    try {
      return Store.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override fun writeTo(t: Store, output: OutputStream) = t.writeTo(output)

}
