package com.kelsos.mbrc.preferences

import android.content.SharedPreferences
import java.util.UUID


class ClientInformationStoreImpl
 constructor(private val preferences: SharedPreferences) : ClientInformationStore {

  override fun getClientId(): String {
    var clientId = preferences.getString(CLIENT_ID, "")

    if (clientId.isBlank()) {
      clientId = UUID.randomUUID().toString()
      preferences.edit().putString(CLIENT_ID, clientId).apply()
    }

    return clientId
  }

  companion object {
    const val CLIENT_ID = "com.kelsos.mbrc.CLIENT_ID"
  }
}