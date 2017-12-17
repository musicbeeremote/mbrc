package com.kelsos.mbrc.content.radios

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import io.reactivex.Observable
import javax.inject.Inject

class RemoteRadioDataSourceImpl
@Inject
constructor(private val radioApi: RadioApi) : RemoteRadioDataSource {
  override fun fetch(): Observable<List<RadioStationDto>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      radioApi.getRadios(it * RemoteDataSource.LIMIT, RemoteDataSource.LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}
