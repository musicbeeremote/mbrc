package com.kelsos.mbrc.preferences

import com.chibatching.kotpref.KotprefModel

object ClientInformationModelImpl : KotprefModel(), ClientInformationModel {
  override var clientId: String by stringPref()
}
