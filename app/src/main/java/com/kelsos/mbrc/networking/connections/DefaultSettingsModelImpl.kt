package com.kelsos.mbrc.networking.connections

import com.chibatching.kotpref.KotprefModel

object DefaultSettingsModelImpl : KotprefModel(), DefaultSettingsModel {
  override var defaultId: Long by longPref()
}
