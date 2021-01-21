package com.kelsos.mbrc.preferences

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.kelsos.mbrc.common.utilities.AppDispatchers
import com.kelsos.mbrc.features.library.PlayingTrack
import com.kelsos.mbrc.preferences.data.Settings
import com.kelsos.mbrc.preferences.data.SettingsSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException

class AppDataStoreImpl(
  context: Application,
  private val dispatchers: AppDispatchers
) : AppDataStore {

  private val dataStore: DataStore<Settings> = DataStoreFactory.create(
    serializer = SettingsSerializer,
    produceFile = {
      File(context.filesDir, "settings.db")
    }
  )

  private val settings: Flow<Settings> = dataStore.data
    .catch { exception ->
      // dataStore.data throws an IOException when an error is encountered when reading data
      if (exception is IOException) {
        Timber.e(exception, "Error reading sort order preferences.")
        emit(Settings.getDefaultInstance())
      } else {
        throw exception
      }
    }

  override suspend fun setClientId(clientId: String) = withContext(dispatchers.io) {
    dataStore.updateData { settings ->
      settings.toBuilder()
        .setClientId(clientId)
        .build()
    }
    return@withContext
  }

  override suspend fun getCliendId(): String = withContext(dispatchers.io) {
    return@withContext dataStore.data.first().clientId
  }

  override suspend fun updateCache(track: PlayingTrack) = withContext(dispatchers.io) {
    dataStore.updateData { settings ->
      val cache = settings.cache.toBuilder()
        .setAlbum(track.album)
        .setArtist(track.artist)
        .setPath(track.path)
        .setTitle(track.title)
        .setYear(track.year)
        .setCover(track.coverUrl)
        .build()

      settings.toBuilder().setCache(cache).build()
    }
    return@withContext
  }

  override suspend fun restoreFromCache() = withContext(dispatchers.io) {
    val cache = settings.first().cache

    return@withContext PlayingTrack(
      cache.artist,
      cache.title,
      cache.album,
      cache.year,
      cache.path,
      cache.cover
    )
  }

  override suspend fun setDefaultConnectionId(id: Long) {
    dataStore.updateData { settings ->
      val userSettings = settings.userSettings.toBuilder()
        .setDefaultId(id)
        .build()
      settings.toBuilder().setUserSettings(userSettings).build()
    }
  }

  override fun getDefaultConnectionId(): Flow<Long> {
    return dataStore.data.map { it.userSettings.defaultId }
  }
}
