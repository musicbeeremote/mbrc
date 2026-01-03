package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.networking.ApiBase
import com.kelsos.mbrc.core.networking.dto.OutputResponse
import com.kelsos.mbrc.core.networking.protocol.base.Protocol

class OutputApiImpl(private val apiBase: ApiBase) : OutputApi {
  override suspend fun getOutputs(): OutputResponse =
    apiBase.getItem(Protocol.PlayerOutput, OutputResponse::class)

  override suspend fun setOutput(active: String): OutputResponse =
    apiBase.getItem(Protocol.PlayerOutputSwitch, OutputResponse::class, active)
}
