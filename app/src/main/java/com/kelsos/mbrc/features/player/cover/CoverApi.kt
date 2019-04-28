package com.kelsos.mbrc.features.player.cover

import io.reactivex.Single

interface CoverApi {
  fun getCover(): Single<String>
}