package com.kelsos.mbrc.content.output

import arrow.core.Either
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol

class OutputApiImpl(
  private val apiBase: ApiBase
) : OutputApi {
  override suspend fun getOutputs(): Either<Throwable, OutputResponse> {
    return Either.catch {
      apiBase.getItem(Protocol.PlayerOutput, OutputResponse::class)
    }
  }

  override suspend fun setOutput(active: String): Either<Throwable, OutputResponse> {
    return Either.catch {
      apiBase.getItem(Protocol.PlayerOutputSwitch, OutputResponse::class)
    }
  }
}