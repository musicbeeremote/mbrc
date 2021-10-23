package com.kelsos.mbrc.features.settings

interface ClientInformationStore {
  suspend fun getClientId(): String
}
