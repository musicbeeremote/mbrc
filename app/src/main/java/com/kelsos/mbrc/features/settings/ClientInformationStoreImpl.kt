package com.kelsos.mbrc.features.settings

import android.content.Context
import kotlinx.coroutines.flow.first
import java.util.UUID

class ClientInformationStoreImpl(
  context: Context,
) : ClientInformationStore {
  private val dataStore = context.dataStore

  override suspend fun getClientId(): String {
    val settings = dataStore.data.first()
    val uuid = settings.app.uuid
    return uuid.ifBlank {
      val newUuid = UUID.randomUUID().toString()
      dataStore.updateData {
        val app =
          settings.app
            .toBuilder()
            .setUuid(newUuid)
            .build()
        settings
          .toBuilder()
          .setApp(app)
          .build()
      }
      newUuid
    }
  }
}
