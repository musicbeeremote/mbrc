package com.kelsos.mbrc.state

import android.app.Application
import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
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

internal val Context.cacheDataStore: DataStore<Store> by dataStore(
  fileName = "cache_store.db",
  serializer = PlayerStateSerializer
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
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
          Timber.e(exception, "Error reading sort order preferences.")
          emit(Store.getDefaultInstance())
        } else {
          throw exception
        }
      }

  override suspend fun persistInfo(playingTrack: TrackInfo) {
    withContext(dispatchers.io) {
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
