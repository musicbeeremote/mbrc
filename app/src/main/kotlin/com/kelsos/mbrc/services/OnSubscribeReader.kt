package com.kelsos.mbrc.services

import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import timber.log.Timber
import java.io.BufferedReader

class OnSubscribeReader(
    private val reader: BufferedReader
) : ObservableOnSubscribe<String> {
  override fun subscribe(observer: ObservableEmitter<String>?) {

    try {
      while (true) {
        val line = reader.readLine()
        if (!line.isNullOrBlank()) {
          Timber.v("incoming -> %s", line)
          observer?.onNext(line)
        } else {
          break
        }
      }
      observer?.onComplete()
    } catch (e: Throwable) {
      observer?.onError(e)
    }
  }
}
