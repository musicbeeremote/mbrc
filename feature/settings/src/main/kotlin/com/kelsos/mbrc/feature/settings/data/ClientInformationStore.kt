package com.kelsos.mbrc.feature.settings.data

import android.app.Application
import androidx.datastore.preferences.core.edit
import com.kelsos.mbrc.feature.settings.data.SettingsDataStore.PreferenceKeys
import com.kelsos.mbrc.feature.settings.data.SettingsDataStore.dataStore
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

fun interface ClientInformationStore {
  suspend fun getClientId(): String
}

class ClientInformationStoreImpl(private val context: Application) : ClientInformationStore {
  override suspend fun getClientId(): String {
    val existingUuid = context.dataStore.data.map { preferences ->
      preferences[PreferenceKeys.CLIENT_UUID] ?: ""
    }.first()

    return existingUuid.ifBlank {
      val newUuid = UUID.randomUUID().toString()
      context.dataStore.edit { preferences ->
        preferences[PreferenceKeys.CLIENT_UUID] = newUuid
      }
      newUuid
    }
  }
}
