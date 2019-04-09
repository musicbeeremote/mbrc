package com.kelsos.mbrc.content.output

import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import io.reactivex.Single

class OutputApiImpl(
  private val apiBase: ApiBase
) : OutputApi {
  override fun getOutputs(): Single<OutputResponse> {
    return apiBase.getItem(Protocol.PlayerOutput, OutputResponse::class)
  }

  override fun setOutput(active: String): Single<OutputResponse> {
    return apiBase.getItem(Protocol.PlayerOutputSwitch, OutputResponse::class)
  }
}