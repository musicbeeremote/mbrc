package com.kelsos.mbrc.core.networking.protocol.models

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

/**
 * Tolerates servers that emit `data: []` (or a bare top-level `[]`) for an
 * empty page instead of the documented `{ total, offset, limit, data: [] }`
 * object. Without this, the generated adapter throws `JsonDataException:
 * Expected BEGIN_OBJECT but was BEGIN_ARRAY` and aborts pagination.
 */
object PageAdapterFactory : JsonAdapter.Factory {
  override fun create(type: Type, annotations: Set<Annotation>, moshi: Moshi): JsonAdapter<*>? {
    if (annotations.isNotEmpty()) return null
    if (Types.getRawType(type) != Page::class.java) return null

    val delegate = moshi.nextAdapter<Any>(this, type, annotations)
    return EmptyArrayTolerantPageAdapter(delegate)
  }

  private class EmptyArrayTolerantPageAdapter(private val delegate: JsonAdapter<Any>) :
    JsonAdapter<Any>() {
    override fun fromJson(reader: JsonReader): Any? {
      if (reader.peek() == JsonReader.Token.BEGIN_ARRAY) {
        reader.skipValue()
        // Synthesize a terminal empty page so ApiBase.getAllPages's
        // `offset + limit > total` break condition fires immediately
        // (1 > 0) instead of looping forever against a server that keeps
        // returning bare arrays.
        return Page<Any>().apply { limit = 1 }
      }
      return delegate.fromJson(reader)
    }

    override fun toJson(writer: JsonWriter, value: Any?) {
      delegate.toJson(writer, value)
    }
  }
}
