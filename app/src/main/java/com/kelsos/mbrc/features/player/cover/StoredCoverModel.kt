package com.kelsos.mbrc.features.player.cover

import com.chibatching.kotpref.KotprefModel

object StoredCoverModel : KotprefModel(), CoverModel {
  override var coverPath by stringPref()
}
