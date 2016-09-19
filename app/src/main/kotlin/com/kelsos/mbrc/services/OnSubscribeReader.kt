package com.kelsos.mbrc.services

import android.text.TextUtils
import rx.Observer
import rx.observables.SyncOnSubscribe
import java.io.BufferedReader
import java.io.IOException

class OnSubscribeReader(private val reader: BufferedReader) : SyncOnSubscribe<BufferedReader, String>() {

  override fun generateState(): BufferedReader {
    return this.reader
  }

  override fun next(state: BufferedReader, observer: Observer<in String>): BufferedReader {
    try {
      val line = reader.readLine()
      if (TextUtils.isEmpty(line)) {
        observer.onCompleted()
      } else {
        observer.onNext(line)
      }
    } catch (e: IOException) {
      observer.onError(e)
    }

    return state
  }
}
