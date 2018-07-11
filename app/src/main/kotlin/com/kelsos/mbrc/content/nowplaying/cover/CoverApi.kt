package com.kelsos.mbrc.content.nowplaying.cover

import io.reactivex.Single

interface CoverApi {
  fun getCover(): Single<String>
}