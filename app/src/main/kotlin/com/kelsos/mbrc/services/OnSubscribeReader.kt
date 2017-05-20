package com.kelsos.mbrc.services

import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import timber.log.Timber
import java.io.BufferedReader
import java.net.Socket

class OnSubscribeReader(
    private val reader: BufferedReader,
    private val socket: Socket
) : ObservableOnSubscribe<ServiceCaller.ServiceMessage> {
  override fun subscribe(observer: ObservableEmitter<ServiceCaller.ServiceMessage>) = try {
    while (true) {
      val line = reader.readLine()
      if (line.isNullOrBlank()) {
        break
      }

      Timber.v("incoming -> %s", line)
      observer.onNext(ServiceCaller.ServiceMessage(line, socket))
    }
    observer.onComplete()
  } catch (e: Throwable) {
    observer.onError(e)
  }
}
