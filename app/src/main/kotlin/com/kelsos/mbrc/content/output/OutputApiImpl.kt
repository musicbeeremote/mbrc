package com.kelsos.mbrc.content.output

import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import javax.inject.Inject

class OutputApiImpl
@Inject
constructor(
  private val apiBase: ApiBase
) : OutputApi {
  override suspend fun getOutputs(): OutputResponse {
    return apiBase.getItem(Protocol.PlayerOutput, OutputResponse::class)
  }

  override suspend fun setOutput(active: String): OutputResponse {
    return apiBase.getItem(Protocol.PlayerOutputSwitch, OutputResponse::class, active)
  }
}
