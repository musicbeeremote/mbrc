package com.kelsos.mbrc.extensions

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.data.Page
import com.kelsos.mbrc.data.SocketMessage
import io.reactivex.Observable
import java.io.IOException

inline fun <reified T> SocketMessage.toPage(objectMapper: ObjectMapper): Observable<Page<T>> {
  return Observable.create<Page<T>> {
    try {
      val typeReference = object : TypeReference<Page<T>>() {}
      val page = objectMapper.readValue<Page<T>>(this.data as String, typeReference)
      it.onNext(page)
      it.onComplete()
    } catch (e: IOException) {
      it.onError(e)
    }
  }
}
