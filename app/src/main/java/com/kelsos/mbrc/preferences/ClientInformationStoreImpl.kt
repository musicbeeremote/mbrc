package com.kelsos.mbrc.preferences

import java.util.UUID

class ClientInformationStoreImpl
constructor(private val model: ClientInformationModel) : ClientInformationStore {

  override fun getClientId(): String {
    var clientId = model.clientId

    if (clientId.isBlank()) {
      clientId = UUID.randomUUID().toString()
      model.clientId = clientId
    }

    return clientId
  }
}