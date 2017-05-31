package com.kelsos.mbrc.content.now_playing.cover

import io.reactivex.Single

interface CoverApi {

  fun getCover(): Single<String>

}
