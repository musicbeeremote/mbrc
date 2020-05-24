package com.kelsos.mbrc.features.output

import arrow.core.Either

interface OutputApi {
  suspend fun getOutputs(): Either<Throwable, OutputResponse>

  suspend fun setOutput(active: String): Either<Throwable, OutputResponse>
}
