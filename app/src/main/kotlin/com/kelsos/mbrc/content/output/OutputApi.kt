package com.kelsos.mbrc.content.output

import io.reactivex.Single

interface OutputApi {
  fun getOutputs(): Single<OutputResponse>

  fun setOutput(active: String): Single<OutputResponse>
}