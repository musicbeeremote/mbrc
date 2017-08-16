package com.kelsos.mbrc.content.output

interface OutputApi {
  suspend fun getOutputs(): OutputResponse

  suspend fun setOutput(active: String): OutputResponse
}
