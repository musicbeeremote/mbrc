package com.kelsos.mbrc.preferences

import android.content.SharedPreferences

class ClientInformationModelImpl(
  private val preferences: SharedPreferences
) : ClientInformationModel {

  override var clientId: String
    get() {
      return preferences.getString(CLIENT_ID, "") ?: ""
    }
    set(value) {
      preferences.edit().putString(CLIENT_ID, value).apply()
    }

  companion object {
    const val CLIENT_ID = "com.kelsos.mbrc.CLIENT_ID"
  }
}
