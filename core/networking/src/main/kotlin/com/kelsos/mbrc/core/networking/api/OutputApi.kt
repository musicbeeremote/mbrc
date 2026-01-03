package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.networking.dto.OutputResponse

interface OutputApi {
  suspend fun getOutputs(): OutputResponse

  suspend fun setOutput(active: String): OutputResponse
}
