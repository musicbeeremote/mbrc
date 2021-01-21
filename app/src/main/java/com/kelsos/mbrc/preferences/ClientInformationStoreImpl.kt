package com.kelsos.mbrc.preferences

import java.util.UUID

class ClientInformationStoreImpl(
  private val dataStore: AppDataStore
) : ClientInformationStore {

  override suspend fun getClientId(): String {
    var clientId = dataStore.getCliendId()

    if (clientId.isBlank()) {
      clientId = UUID.randomUUID().toString()
      dataStore.setClientId(clientId)
    }

    return clientId
  }
}
