package com.kelsos.mbrc.services.api

import com.kelsos.mbrc.dto.BaseResponse
import retrofit2.http.GET
import rx.Observable

interface ApiService {
  @GET("/status")
  fun getStatus(): Observable<BaseResponse>
}
