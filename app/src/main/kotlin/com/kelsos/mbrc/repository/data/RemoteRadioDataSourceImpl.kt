package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.services.RadioService
import rx.Observable
import javax.inject.Inject

class RemoteRadioDataSourceImpl
@Inject constructor(private val radioService: RadioService) : RemoteRadioDataSource {
  override fun fetch(): Observable<List<RadioStation>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      radioService.getRadios(it!! * RemoteDataSource.LIMIT, RemoteDataSource.LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}
