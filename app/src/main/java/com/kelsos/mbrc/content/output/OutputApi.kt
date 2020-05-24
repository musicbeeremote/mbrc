package com.kelsos.mbrc.content.output

import arrow.core.Either

interface OutputApi {
  suspend fun getOutputs(): Either<Throwable, OutputResponse>

  suspend fun setOutput(active: String): Either<Throwable, OutputResponse>
}