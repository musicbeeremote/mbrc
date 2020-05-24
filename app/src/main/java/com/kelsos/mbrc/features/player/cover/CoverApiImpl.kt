package com.kelsos.mbrc.features.player.cover

import arrow.core.Either
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol

class CoverApiImpl(
  private val apiBase: ApiBase
) : CoverApi {
  override suspend fun getCover(): Either<Throwable, String> = Either.catch {
    val payload = apiBase.getItem(Protocol.NowPlayingCover, CoverPayload::class)
    if (payload.status == CoverPayload.SUCCESS) {
      payload.cover
    } else {
      throw RuntimeException("Cover not available")
    }
  }
}