package com.kelsos.mbrc.content.activestatus

import android.app.Application
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.features.library.PlayingTrack
import com.squareup.moshi.Moshi
import java.io.File
import java.nio.charset.Charset
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source

class PlayingTrackCacheImpl(
  private val mapper: Moshi,
  private val appContext: Application,
  private val dispatchers: AppCoroutineDispatchers
) : PlayingTrackCache {

  private val adapter by lazy { mapper.adapter(PlayingTrack::class.java) }

  override suspend fun persistInfo(track: PlayingTrack) {
    withContext(dispatchers.disk) {
      val infoFile = File(appContext.filesDir, TRACK_INFO)
      if (infoFile.exists()) {
        infoFile.delete()
      }
      adapter.toJson(infoFile.sink().buffer(), track)
    }
  }

  override suspend fun restoreInfo(): PlayingTrack? {
    return withContext(dispatchers.disk) {
      val infoFile = File(appContext.filesDir, TRACK_INFO)
      return@withContext if (infoFile.exists()) {
        adapter.fromJson(infoFile.source().buffer())
      } else {
        null
      }
    }
  }

  override suspend fun persistCover(cover: String) {
    withContext(dispatchers.disk) {
      val coverFile = File(appContext.filesDir, COVER_INFO)
      if (coverFile.exists()) {
        coverFile.delete()
      }

      coverFile.sink().buffer().use {
        it.write(coverFile.readBytes())
        it.emit()
        it.close()
      }
    }
  }

  override suspend fun restoreCover(): String? {
    return withContext(dispatchers.disk) {
      val coverFile = File(appContext.filesDir, COVER_INFO)
      return@withContext if (!coverFile.exists()) {
        null
      } else {
        coverFile.source().buffer().use {
          return@use it.readString(Charset.forName("UTF-8"))
        }
      }
    }
  }

  companion object {
    const val TRACK_INFO = "track.json"
    const val COVER_INFO = "cover.txt"
  }
}

interface PlayingTrackCache {
  suspend fun persistInfo(track: PlayingTrack)
  suspend fun restoreInfo(): PlayingTrack?
  suspend fun persistCover(cover: String)
  suspend fun restoreCover(): String?
}