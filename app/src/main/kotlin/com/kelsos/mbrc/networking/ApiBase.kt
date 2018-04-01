package com.kelsos.mbrc.networking

import com.fasterxml.jackson.databind.ObjectMapper
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.connections.ConnectionRepository
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.networking.protocol.PageRange
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.networking.protocol.Protocol.Context
import com.kelsos.mbrc.networking.protocol.ProtocolPayload
import com.kelsos.mbrc.preferences.ClientInformationStore
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import kotlin.reflect.KClass

class ApiBase
@Inject constructor(
  repository: ConnectionRepository,
  private val mapper: ObjectMapper,
  private val clientInformationStore: ClientInformationStore
) : ApiRequestBase(mapper, repository) {

  fun <T> getItem(@Context request: String, clazz: Class<T>, payload: Any = ""): Single<T> {
    return request(request, payload).map { mapper.convertValue(it.data, clazz) }
      .firstOrError()
  }

  //todo change it so that a socket connection is running longer

  fun <T : Any> getAllPages(@Context request: String, clazz: KClass<T>): Observable<List<T>> {
    val start = System.currentTimeMillis()
    val type = mapper.typeFactory.constructParametricType(Page::class.java, clazz.java)

    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      val pageStart = System.currentTimeMillis()
      val offset = it * RemoteDataSource.LIMIT
      Timber.v("fetching $request offset $offset - limit ${RemoteDataSource.LIMIT}")
      val range = getPageRange(offset, RemoteDataSource.LIMIT)
      request(request, range).map {
        mapper.convertValue<Page<T>>(it.data, type)
      }.doOnComplete {
        Timber.v("duration ${System.currentTimeMillis() - pageStart} ms")
      }
    }.takeWhile { it.offset < it.total }
      .map { it.data }
      .doOnComplete { Timber.v("duration ${System.currentTimeMillis() - start} ms") }
  }

  private fun request(@Context request: String, data: Any? = null): Observable<SocketMessage> {
    return call().flatMap { getSocketMessageObservable(request, data ?: "", it) }
      .skipWhile { it.shouldSkip() }
  }

  private fun getSocketMessageObservable(
    request: String,
    data: Any,
    serviceMessage: ServiceMessage
  ): Observable<SocketMessage> {
    return Observable.create<SocketMessage> {
      try {
        val socket = serviceMessage.socket
        val jsonString = serviceMessage.message
        val message = mapper.readValue(jsonString, SocketMessage::class.java)
        val context = message.context

        Timber.v("$context message received")
        if (Protocol.Player == context) {
          val payload = getProtocolPayload()
          socket.send(SocketMessage.create(Protocol.ProtocolTag, payload))
        } else if (Protocol.ProtocolTag == context) {
          socket.send(SocketMessage.create(request, data))
        }

        it.onNext(message)
        it.onComplete()
      } catch (e: IOException) {
        it.tryOnError(e)
      }
    }
  }

  private fun getProtocolPayload(): ProtocolPayload {
    return ProtocolPayload(clientInformationStore.getClientId()).apply {
      noBroadcast = true
      protocolVersion = Protocol.ProtocolVersionNumber
    }
  }

  private fun SocketMessage.shouldSkip(): Boolean {
    return Protocol.Player == this.context || Protocol.ProtocolTag == this.context
  }

  private fun getPageRange(offset: Int, limit: Int): PageRange? {
    var range: PageRange? = null

    if (limit > 0) {
      range = PageRange()
      range.offset = offset
      range.limit = limit
    }
    return range
  }
}