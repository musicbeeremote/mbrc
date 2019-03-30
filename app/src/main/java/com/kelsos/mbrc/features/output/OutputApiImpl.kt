package com.kelsos.mbrc.features.output

import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol

class OutputApiImpl(
  private val apiBase: ApiBase
) : OutputApi {
  override suspend fun getOutputs(): OutputResponse {
    return apiBase.getItem(Protocol.PlayerOutput, OutputResponse::class)
  }

  override suspend fun setOutput(active: String): OutputResponse {
    return apiBase.getItem(Protocol.PlayerOutputSwitch, OutputResponse::class, active)
  }
}
