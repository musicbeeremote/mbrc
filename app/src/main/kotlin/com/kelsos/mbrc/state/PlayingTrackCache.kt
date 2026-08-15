package com.kelsos.mbrc.state

import android.app.Application
import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.kelsos.mbrc.core.common.state.BasicTrackInfo
import com.kelsos.mbrc.core.common.state.TrackInfo
import com.kelsos.mbrc.core.common.utilities.coroutines.AppCoroutineDispatchers
import com.kelsos.mbrc.store.Store
import com.kelsos.mbrc.store.Track
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Cache of the last playing track.
 *
 * The corruption handler is what keeps a damaged file from being fatal. [PlayerStateSerializer]
 * turns an unparseable file into a [CorruptionException], but that only tells DataStore what went
 * wrong; without a handler DataStore rethrows it out of every read and write, and since nothing
 * ever rewrites the file the app crashes on the same bytes on every launch (#343). Replacing the
 * contents costs nothing here, because this is a cache that the next track change refills.
 */
internal val Context.cacheDataStore: DataStore<Store> by dataStore(
  fileName = "cache_store.db",
  serializer = PlayerStateSerializer,
  corruptionHandler = ReplaceFileCorruptionHandler { exception ->
    // Logged so a repair is visible at all: it is otherwise silent, and the cause is worth
    // knowing if it turns out to happen to more than one person.
    Timber.e(exception, "The playing track cache was unreadable and has been rebuilt")
    Store.getDefaultInstance()
  }
)

interface PlayingTrackCache {
  suspend fun persistInfo(playingTrack: TrackInfo)

  suspend fun restoreInfo(): TrackInfo
}

class PlayingTrackCacheImpl(
  private val context: Application,
  private val dispatchers: AppCoroutineDispatchers
) : PlayingTrackCache {
  private val storeFlow: Flow<Store> =
    context.cacheDataStore.data
      .catch { exception ->
        // Corruption is repaired by the handler on the DataStore itself; this covers the
        // transient read failures (permissions, no space) that leave the file intact.
        if (exception is IOException) {
          Timber.e(exception, "Error reading the playing track cache")
          emit(Store.getDefaultInstance())
        } else {
          throw exception
        }
      }

  override suspend fun persistInfo(playingTrack: TrackInfo) {
    withContext(dispatchers.io) {
      // The read path already degrades to a default on failure; the write path had nothing, so a
      // failure here propagated out of the collector that calls it and took the app down. Losing
      // the cached track is not worth a crash.
      try {
        context.cacheDataStore.updateData { store ->
          val track = Track.newBuilder()
            .setAlbum(playingTrack.album)
            .setArtist(playingTrack.artist)
            .setPath(playingTrack.path)
            .setTitle(playingTrack.title)
            .setYear(playingTrack.year)
            .build()

          store.toBuilder()
            .setTrack(track)
            .setCover(playingTrack.coverUrl)
            .build()
        }
      } catch (e: IOException) {
        Timber.e(e, "Failed to persist the playing track")
      }
    }
  }

  override suspend fun restoreInfo(): TrackInfo = withContext(dispatchers.io) {
    val store = storeFlow.first()
    val track = store.track
    BasicTrackInfo(
      artist = track.artist,
      title = track.title,
      album = track.album,
      year = track.year,
      path = track.path,
      coverUrl = store.cover
    )
  }
}

object PlayerStateSerializer : Serializer<Store> {
  override suspend fun readFrom(input: InputStream): Store {
    try {
      return Store.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("Cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: Store, output: OutputStream) {
    t.writeTo(output)
  }

  override val defaultValue: Store
    get() = Store.getDefaultInstance()
}
