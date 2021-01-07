package com.kelsos.mbrc.features.player.cover

import arrow.core.Either

interface CoverApi {
  suspend fun getCover(): Either<Throwable, String>
}
