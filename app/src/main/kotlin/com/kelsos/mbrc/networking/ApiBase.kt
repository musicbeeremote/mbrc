package com.kelsos.mbrc.networking

import com.kelsos.mbrc.DeserializationAdapter
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.client.GenericSocketMessage
import com.kelsos.mbrc.networking.client.SocketMessage
import com.kelsos.mbrc.networking.protocol.Page
import com.kelsos.mbrc.networking.protocol.PageRange
import com.kelsos.mbrc.networking.protocol.Protocol.Context
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass

class ApiBase
@Inject constructor(
  private val deserializationAdapter: DeserializationAdapter,
  private val apiRequestManager: RequestManager
) {

  fun <T> getItem(
    @Context request: String,
    kClazz: KClass<T>,
    payload: Any = ""
  ): Single<T> where T : Any {
    val factory = deserializationAdapter.typeFactory()
    val type = factory.constructParametricType(GenericSocketMessage::class.java, kClazz.java)
    return Single.fromCallable { apiRequestManager.openConnection() }.flatMap {
      apiRequestManager.request(it, SocketMessage.create(request, payload)).map {
        val socketMessage = deserializationAdapter.objectify<GenericSocketMessage<T>>(it, type)
        socketMessage.data
      }.doFinally { it.close() }
    }
  }

  fun <T : Any> getAllPages(@Context request: String, clazz: KClass<T>): Observable<List<T>> {
    val start = now()

    val factory = deserializationAdapter.typeFactory()
    val inner = factory.constructParametricType(Page::class.java, clazz.java)
    val type = factory.constructParametricType(GenericSocketMessage::class.java, inner)

    return Single.fromCallable { apiRequestManager.openConnection() }
      .flatMapObservable { connection ->
        Observable.range(0, Integer.MAX_VALUE).flatMapSingle {
          val pageStart = now()

          val limit = RemoteDataSource.LIMIT
          val offset = it * limit
          val range = getPageRange(offset, limit)

          Timber.v("fetching $request offset $offset [$limit]")

          val message = SocketMessage.create(request, range ?: "")

          apiRequestManager.request(connection, message).map {
            deserializationAdapter.objectify<GenericSocketMessage<Page<T>>>(it, type).data
          }.doOnSuccess {
            Timber.v("duration ${now() - pageStart} ms")
          }
        }.takeWhile { it.offset < it.total }
          .map { it.data }
          .doFinally {
            connection.close()
          }
          .doOnComplete { Timber.v("duration ${System.currentTimeMillis() - start} ms") }
      }
  }

  private fun getPageRange(offset: Int, limit: Int): PageRange? {
    return takeIf { limit > 0 }?.run {
      PageRange().apply {
        this.offset = offset
        this.limit = limit
      }
    }
  }

  private fun now(): Long {
    return System.currentTimeMillis()
  }
}