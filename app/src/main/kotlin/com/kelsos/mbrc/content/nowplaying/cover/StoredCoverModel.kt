package com.kelsos.mbrc.content.nowplaying.cover

import com.chibatching.kotpref.KotprefModel

object StoredCoverModel : KotprefModel(), CoverModel {
  override var coverPath by stringPref()
}