package com.kelsos.mbrc.networking

import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import timber.log.Timber
import java.io.BufferedReader
import java.net.Socket

class ProtocolResponseReader(
    private val reader: BufferedReader,
    private val socket: Socket
) : ObservableOnSubscribe<ApiRequestBase.ServiceMessage> {
  override fun subscribe(observer: ObservableEmitter<ApiRequestBase.ServiceMessage>) = try {
    while (true) {
      val line = reader.readLine()
      if (line.isNullOrBlank()) {
        break
      }

      Timber.v("incoming -> %s", line)
      observer.onNext(ApiRequestBase.ServiceMessage(line, socket))
    }
    observer.onComplete()
  } catch (e: Throwable) {
    Timber.v(e, "Reader encountered an exception")
    observer.let {
      if (!it.isDisposed) {
        observer.onError(e)
      }
    }
  }
}
