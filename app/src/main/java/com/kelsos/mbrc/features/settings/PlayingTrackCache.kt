package com.kelsos.mbrc.features.settings

import android.app.Application
import androidx.datastore.core.DataStore
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.PlayingTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

class PlayingTrackCacheImpl(
  context: Application,
  private val dispatchers: AppCoroutineDispatchers
) : PlayingTrackCache {

  private val dataStore: DataStore<Settings> = context.dataStore
  private val settingsFlow: Flow<Settings> = dataStore.data
    .catch { exception ->
      // dataStore.data throws an IOException when an error is encountered when reading data
      if (exception is IOException) {
        Timber.e(exception, "Error reading sort order preferences.")
        emit(Settings.getDefaultInstance())
      } else {
        throw exception
      }
    }

  override suspend fun persistInfo(track: PlayingTrack) = withContext(dispatchers.io) {
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

  override suspend fun restoreInfo(): PlayingTrack = withContext(dispatchers.io) {
    val cache = settingsFlow.first().cache

    return@withContext PlayingTrack(
      cache.artist,
      cache.title,
      cache.album,
      cache.year,
      cache.path,
      cache.cover
    )
  }
}

interface PlayingTrackCache {
  suspend fun persistInfo(track: PlayingTrack)
  suspend fun restoreInfo(): PlayingTrack
}
