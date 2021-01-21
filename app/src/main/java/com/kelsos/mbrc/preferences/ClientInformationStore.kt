package com.kelsos.mbrc.preferences

interface ClientInformationStore {
  suspend fun getClientId(): String
}
